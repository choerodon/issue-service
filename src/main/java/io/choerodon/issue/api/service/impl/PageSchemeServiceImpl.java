package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.PageIssueTypeSchemeDTO;
import io.choerodon.issue.api.dto.PageSchemeDTO;
import io.choerodon.issue.api.dto.PageSchemeDetailDTO;
import io.choerodon.issue.api.dto.PageSchemeLineDTO;
import io.choerodon.issue.api.service.PageSchemeLineService;
import io.choerodon.issue.api.service.PageSchemeService;
import io.choerodon.issue.domain.PageIssueScheme;
import io.choerodon.issue.domain.PageScheme;
import io.choerodon.issue.infra.enums.PageSchemeLineType;
import io.choerodon.issue.infra.mapper.PageIssueSchemeMapper;
import io.choerodon.issue.infra.mapper.PageMapper;
import io.choerodon.issue.infra.mapper.PageSchemeLineMapper;
import io.choerodon.issue.infra.mapper.PageSchemeMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/23
 */
@Component
@RefreshScope
public class PageSchemeServiceImpl extends BaseServiceImpl<PageScheme> implements PageSchemeService {

    @Autowired
    private PageSchemeMapper pageSchemeMapper;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private PageSchemeLineMapper pageSchemeLineMapper;

    @Autowired
    private PageSchemeLineService pageSchemeLineService;

    @Autowired
    private PageIssueSchemeMapper pageIssueSchemeMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public PageSchemeDetailDTO queryById(Long organizationId, Long pageSchemeId) {
        PageScheme pageScheme = pageSchemeMapper.selectByPrimaryKey(pageSchemeId);
        if (pageScheme != null) {
            PageSchemeDetailDTO pageSchemeDetailDTO = modelMapper.map(pageScheme, PageSchemeDetailDTO.class);
            //根据方案配置
            List<PageSchemeLineDTO> lines = modelMapper.map(pageSchemeLineMapper.queryBySchemeId(organizationId, pageSchemeId), new TypeToken<List<PageSchemeLineDTO>>() {
            }.getType());
            for (PageSchemeLineDTO line : lines) {
                if (line.getType().equals(PageSchemeLineType.DEFAULT.value())) {
                    line.setDefault(true);
                }
            }
            pageSchemeDetailDTO.setPageSchemeLineDTOS(lines);
            return pageSchemeDetailDTO;
        }
        return null;
    }

    @Override
    @Transactional
    public PageSchemeDetailDTO create(Long organizationId, PageSchemeDetailDTO pageSchemeDetailDTO) {

        if (!checkName(organizationId, pageSchemeDetailDTO.getName(), null)) {
            throw new CommonException("error.pageScheme.name.exist");
        }

        pageSchemeDetailDTO.setOrganizationId(organizationId);
        PageScheme pageScheme = modelMapper.map(pageSchemeDetailDTO, PageScheme.class);
        if (pageSchemeMapper.insert(pageScheme) != 1) {
            throw new CommonException("error.pageScheme.create");
        }
        //创建方案配置
        createConfig(organizationId, pageScheme.getId(), pageSchemeDetailDTO.getPageSchemeLineDTOS());

        return queryById(organizationId, pageScheme.getId());
    }

    @Override
    @Transactional
    public PageSchemeDetailDTO update(Long organizationId, PageSchemeDetailDTO pageSchemeDetailDTO) {

        if (pageSchemeDetailDTO.getName() != null && !checkName(organizationId, pageSchemeDetailDTO.getName(), pageSchemeDetailDTO.getId())) {
            throw new CommonException("error.pageScheme.name.exist");
        }

        PageScheme pageScheme = modelMapper.map(pageSchemeDetailDTO, PageScheme.class);
        int isUpdate = pageSchemeMapper.updateByPrimaryKeySelective(pageScheme);
        if (isUpdate != 1) {
            throw new CommonException("error.pageScheme.update");
        }
        //更新方案配置,等待校验[toDo]

        pageSchemeLineMapper.deleteBySchemeId(organizationId, pageSchemeDetailDTO.getId());
        createConfig(organizationId, pageScheme.getId(), pageSchemeDetailDTO.getPageSchemeLineDTOS());

        return queryById(organizationId, pageScheme.getId());
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long pageSchemeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        PageScheme pageScheme = pageSchemeMapper.selectByPrimaryKey(pageSchemeId);
        if (pageScheme == null) {
            throw new CommonException("error.pageScheme.notFound");
        }
        if (!pageScheme.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.pageScheme.illegal");
        }

        //判断是否关联问题类型页面方案
        List<PageIssueScheme> pageIssueSchemes = pageIssueSchemeMapper.queryByPageSchemeId(organizationId, pageSchemeId);
        if (pageIssueSchemes != null && !pageIssueSchemes.isEmpty()) {
            result.put("pageIssueSchemes", pageIssueSchemes);
            result.put("canDelete", false);
        }

        //判断要删除的pageScheme是否有使用中的项目toDo


        return result;
    }

    @Override
    public Boolean delete(Long organizationId, Long pageSchemeId) {
        Map<String, Object> result = checkDelete(organizationId, pageSchemeId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = pageSchemeMapper.deleteByPrimaryKey(pageSchemeId);
            if (isDelete != 1) {
                throw new CommonException("error.pageScheme.delete");
            }
            pageSchemeLineMapper.deleteBySchemeId(organizationId, pageSchemeId);
            //关联删除一些东西【toDo】
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Page<PageSchemeDTO> pageQuery(Long organizationId, PageRequest pageRequest, PageSchemeDTO pageSchemeDTO, String param) {
        PageScheme pageScheme = modelMapper.map(pageSchemeDTO, PageScheme.class);

        Page<PageScheme> pages = PageHelper.doPageAndSort(pageRequest,
                () -> pageSchemeMapper.fulltextSearch(pageScheme, param));
        List<PageScheme> content = pages.getContent();
        List<PageSchemeDTO> contentDTO = modelMapper.map(content, new TypeToken<List<PageSchemeDTO>>() {
        }.getType());

        //关联问题类型页面方案，及是否可删除
        for (PageSchemeDTO pd : contentDTO) {
            Map<String, Object> result = checkDelete(organizationId, pd.getId());
            pd.setCanDelete((Boolean) result.get("canDelete"));
            pd.setPageIssueSchemeDTOS(result.get("pageIssueSchemes") != null ? modelMapper.map(result.get("pageIssueSchemes"), new TypeToken<List<PageIssueTypeSchemeDTO>>() {
            }.getType()) : null);
        }

        Page<PageSchemeDTO> pagesDTO = new Page<>();
        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(contentDTO);
        return pagesDTO;
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        PageScheme select = new PageScheme();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = pageSchemeMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public void createConfig(Long organizationId, Long pageSchemeId, List<PageSchemeLineDTO> pageSchemeLineDTOS) {
        if (pageSchemeLineDTOS != null && !pageSchemeLineDTOS.isEmpty()) {
            for (PageSchemeLineDTO pageSchemeLineDTO : pageSchemeLineDTOS) {
                pageSchemeLineDTO.setSchemeId(pageSchemeId);
                pageSchemeLineService.create(organizationId, pageSchemeLineDTO);
            }
        } else {
            throw new CommonException("error.pageSchemeLine.null");
        }
    }

    @Override
    public List<PageSchemeDetailDTO> queryAll(Long organizationId) {
        PageScheme pageScheme = new PageScheme();
        pageScheme.setOrganizationId(organizationId);
        List<PageScheme> pageSchemes = select(pageScheme);
        if (pageSchemes != null) {
            return modelMapper.map(pageSchemes, new TypeToken<List<PageSchemeLineDTO>>() {
            }.getType());
        }
        return Collections.emptyList();
    }
}
