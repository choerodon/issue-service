package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.PageIssueSchemeDTO;
import io.choerodon.issue.api.dto.PageIssueSchemeLineDTO;
import io.choerodon.issue.api.service.PageIssueSchemeLineService;
import io.choerodon.issue.api.service.PageIssueSchemeService;
import io.choerodon.issue.domain.PageIssueSchemeLine;
import io.choerodon.issue.infra.mapper.PageIssueSchemeLineMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Component
public class PageIssueSchemeLineServiceImpl extends BaseServiceImpl<PageIssueSchemeLine> implements PageIssueSchemeLineService {

    @Autowired
    private PageIssueSchemeService schemeService;

    @Autowired
    private PageIssueSchemeLineMapper lineMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public PageIssueSchemeDTO create(Long organizationId, Long schemeId, PageIssueSchemeLineDTO lineDTO) {
        PageIssueSchemeLine line = modelMapper.map(lineDTO, PageIssueSchemeLine.class);
        line.setSchemeId(schemeId);
        int isInsert = lineMapper.insert(line);
        if (isInsert != 1) {
            throw new CommonException("error.pageIssueSchemeLine.create");
        }
        return schemeService.querySchemeWithConfigById(organizationId, schemeId);
    }

    @Override
    public PageIssueSchemeDTO update(Long organizationId, Long schemeId, PageIssueSchemeLineDTO lineDTO) {
        lineDTO.setSchemeId(schemeId);
        PageIssueSchemeLine line = modelMapper.map(lineDTO, PageIssueSchemeLine.class);
        int isUpdate = lineMapper.updateByPrimaryKeySelective(line);
        if (isUpdate != 1) {
            throw new CommonException("error.pageIssueSchemeLine.update");
        }
        return schemeService.querySchemeWithConfigById(organizationId, schemeId);
    }

    @Override
    public PageIssueSchemeDTO delete(Long organizationId, Long lineId) {
        PageIssueSchemeLine line = lineMapper.selectByPrimaryKey(lineId);
        if (line == null){
            throw new CommonException("error.pageIssueSchemeLine.delete.noFound");
        }
        int isDelete = lineMapper.deleteByPrimaryKey(lineId);
        if (isDelete != 1) {
            throw new CommonException("error.pageIssueSchemeLine.delete");
        }
        return schemeService.querySchemeWithConfigById(organizationId, line.getSchemeId());
    }

    @Override
    public Long getPageSchemeIdByIssueTypeId(Long schemeId, Long issueTypeId) {
        PageIssueSchemeLine line = new PageIssueSchemeLine();
        line.setSchemeId(schemeId);
        line.setIssueTypeId(issueTypeId);
        List<PageIssueSchemeLine> lines = lineMapper.select(line);
        if(lines.isEmpty()){
            line.setIssueTypeId(0L);
            lines = lineMapper.select(line);
            if(lines.isEmpty()){
                return null;
            }
        }
        return lines.get(0).getPageSchemeId();
    }
}
