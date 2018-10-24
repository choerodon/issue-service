package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.PageIssueSchemeDTO;
import io.choerodon.issue.api.dto.PageIssueSchemeLineDTO;
import io.choerodon.issue.api.service.PageIssueSchemeService;
import io.choerodon.issue.api.validator.PageIssueSchemeLineValidator;
import io.choerodon.issue.domain.PageIssueScheme;
import io.choerodon.issue.domain.PageIssueSchemeLine;
import io.choerodon.issue.infra.mapper.PageIssueSchemeLineMapper;
import io.choerodon.issue.infra.mapper.PageIssueSchemeMapper;
import io.choerodon.issue.infra.utils.ListChangeUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Component
public class PageIssueSchemeServiceImpl extends BaseServiceImpl<PageIssueScheme> implements PageIssueSchemeService {

    @Autowired
    private PageIssueSchemeMapper schemeMapper;

    @Autowired
    private PageIssueSchemeLineMapper lineMapper;

    @Autowired
    private PageIssueSchemeLineValidator lineValidator;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<PageIssueSchemeDTO> pageQuery(PageRequest pageRequest, PageIssueSchemeDTO schemeDTO, String params) {
        PageIssueScheme scheme = modelMapper.map(schemeDTO, PageIssueScheme.class);
        Page<PageIssueScheme> page = PageHelper.doPageAndSort(pageRequest,
                () -> schemeMapper.fulltextSearch(scheme, params));
        List<PageIssueScheme> schemes = page.getContent();
        List<PageIssueSchemeDTO> schemeDTOS = modelMapper.map(schemes, new TypeToken<List<PageIssueSchemeDTO>>(){}.getType());

        Page<PageIssueSchemeDTO> returnPage = new Page<>();
        returnPage.setContent(schemeDTOS);
        returnPage.setNumber(page.getNumber());
        returnPage.setNumberOfElements(page.getNumberOfElements());
        returnPage.setSize(page.getSize());
        returnPage.setTotalElements(page.getTotalElements());
        returnPage.setTotalPages(page.getTotalPages());
        return returnPage;
    }

    @Override
    @Transactional
    public PageIssueSchemeDTO create(Long organizationId, PageIssueSchemeDTO schemeDTO) {
        PageIssueScheme scheme = modelMapper.map(schemeDTO, PageIssueScheme.class);
        scheme.setOrganizationId(organizationId);
        int isInsert = schemeMapper.insert(scheme);
        if (isInsert != 1) {
            throw new CommonException("error.pageIssueScheme.create");
        }
        List<PageIssueSchemeLineDTO> lineDTOS = schemeDTO.getLineDTOS();
        if (lineDTOS != null && !lineDTOS.isEmpty()){
            List<PageIssueSchemeLine> lines = modelMapper.map(lineDTOS, new TypeToken<List<PageIssueSchemeLine>>(){}.getType());
            for (PageIssueSchemeLine line:lines) {
                lineValidator.validate(line);
                line.setSchemeId(scheme.getId());
            }
            int isInsertList = lineMapper.insertList(lines);
            if (isInsertList != lines.size()){
                throw new CommonException("error.pageIssueSchemeLine.create");
            }
        }
        return querySchemeWithConfigById(organizationId, scheme.getId());
    }

    @Override
    @Transient
    public PageIssueSchemeDTO update(Long organizationId, Long schemeId, PageIssueSchemeDTO schemeDTO) {
        PageIssueScheme pageIssueScheme = schemeMapper.querySchemeWithConfigById(schemeId);
        if (pageIssueScheme == null){
            throw new CommonException("error.pageIssueScheme.update.noFound");
        }
        //更新头
        schemeDTO.setId(schemeId);
        schemeDTO.setOrganizationId(organizationId);
        PageIssueScheme scheme = modelMapper.map(schemeDTO, PageIssueScheme.class);
        int isUpdate = schemeMapper.updateByPrimaryKeySelective(scheme);
        if (isUpdate != 1) {
            throw new CommonException("error.pageIssueScheme.update");
        }

        List<PageIssueSchemeLine> oldLines = pageIssueScheme.getLines();
        List<PageIssueSchemeLineDTO> lineDTOS = schemeDTO.getLineDTOS();
        if (lineDTOS == null){
            lineDTOS = Collections.emptyList();
        }
        List<PageIssueSchemeLine> newLines = modelMapper.map(lineDTOS, new TypeToken<List<PageIssueSchemeLine>>(){}.getType());

        //获取减少的对象,进行删除
        BiPredicate<PageIssueSchemeLine, PageIssueSchemeLine> myEquals = (PageIssueSchemeLine line1, PageIssueSchemeLine line2) -> {
            if (line1.getId() == null || !line1.getId().equals(line2.getId())) {
                return false;
            }else {
                return true;
            }
        };
        List<PageIssueSchemeLine> reduce = ListChangeUtil.getReduceList(newLines, oldLines, myEquals);
        for (PageIssueSchemeLine red : reduce) {
            //todo 删除合法性
            int isDel = lineMapper.deleteByPrimaryKey(red.getId());
            if (isDel != 1) {
                throw new CommonException("error.pageIssueSchemeLine.delete");
            }
        }
        if (!newLines.isEmpty()){
            for (PageIssueSchemeLine line:newLines) {
                lineValidator.validate(line);
                line.setSchemeId(scheme.getId());
                if (line.getId() != null){
                    int isLineUpdate = lineMapper.updateByPrimaryKey(line);
                    if (isLineUpdate != 1){
                        throw new CommonException("error.pageIssueSchemeLine.update");
                    }
                }else {
                    int isLineInsert = lineMapper.insert(line);
                    if (isLineInsert != 1){
                        throw new CommonException("error.pageIssueSchemeLine.create");
                    }
                }
            }
        }
        return querySchemeWithConfigById(organizationId, scheme.getId());
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    public Boolean delete(Long organizationId, Long schemeId) {
        if (schemeId == null) {
            throw new CommonException("error.pageIssueScheme.delete.schemeId.null");
        }
        int isDelete = schemeMapper.deleteByPrimaryKey(schemeId);
        if (isDelete != 1) {
            throw new CommonException("error.pageIssueScheme.delete");
        }
        //删除方案配置信息
        PageIssueSchemeLine line = new PageIssueSchemeLine();
        line.setSchemeId(schemeId);
        lineMapper.delete(line);
        return true;
    }

    @Override
    public Boolean checkName(Long organizationId, Long schemeId, String name) {
        PageIssueScheme scheme = new PageIssueScheme();
        scheme.setOrganizationId(organizationId);
        scheme.setName(name);
        scheme = schemeMapper.selectOne(scheme);
        if (scheme != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验
            return scheme.getId().equals(schemeId);
        }
        return true;
    }

    @Override
    public PageIssueSchemeDTO querySchemeWithConfigById(Long organizationId, Long schemeId) {
        PageIssueScheme scheme = schemeMapper.querySchemeWithConfigById(schemeId);
        if (scheme == null) {
            throw new CommonException("error.stateMachineScheme.querySchemeWithConfigById.notFound");
        }
        PageIssueSchemeDTO schemeDTO = modelMapper.map(scheme, PageIssueSchemeDTO.class);
        if (scheme.getLines() != null){
            List<PageIssueSchemeLineDTO> lineDTOS = modelMapper.map(scheme.getLines(), new TypeToken<List<PageIssueSchemeLineDTO>>(){}.getType());
            schemeDTO.setLineDTOS(lineDTOS);
        }
        return schemeDTO;
    }

}
