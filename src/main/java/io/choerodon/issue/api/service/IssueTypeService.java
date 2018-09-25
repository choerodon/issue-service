package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/8
 */
public interface IssueTypeService extends BaseService<IssueType> {


    IssueTypeDTO queryById(Long organizationId, Long issueTypeId);

    IssueTypeDTO create(Long organizationId, IssueTypeDTO issueTypeDTO);

    IssueTypeDTO update(IssueTypeDTO issueTypeDTO);

    Boolean delete(Long organizationId, Long issueTypeId);

    Map<String, Object> checkDelete(Long organizationId, Long issueTypeId);

    Page<IssueTypeDTO> pageQuery(PageRequest pageRequest, IssueTypeDTO issueTypeDTO, String param);

    Boolean checkName(Long organizationId, String name, Long id);

    List<IssueTypeDTO> queryByOrgId(Long organizationId);

    /**
     * 查询组织下未绑定到该方案的问题类型
     * @param organizationId 组织id
     * @return 问题类型列表
     */
    List<IssueTypeDTO> queryIssueType(Long organizationId, Long schemeId);
}
