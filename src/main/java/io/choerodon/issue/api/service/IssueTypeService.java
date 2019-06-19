package io.choerodon.issue.api.service;

import io.choerodon.base.domain.PageRequest;
import com.github.pagehelper.PageInfo;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.IssueTypeSearchDTO;
import io.choerodon.issue.api.dto.IssueTypeWithInfoDTO;
import io.choerodon.issue.domain.IssueType;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/8
 */
public interface IssueTypeService {


    IssueTypeDTO queryById(Long organizationId, Long issueTypeId);

    IssueTypeDTO create(Long organizationId, IssueTypeDTO issueTypeDTO);

    IssueTypeDTO update(IssueTypeDTO issueTypeDTO);

    Boolean delete(Long organizationId, Long issueTypeId);

    Map<String, Object> checkDelete(Long organizationId, Long issueTypeId);

    PageInfo<IssueTypeWithInfoDTO> queryIssueTypeList(PageRequest pageRequest, Long organizationId, IssueTypeSearchDTO issueTypeSearchDTO);

    Boolean checkName(Long organizationId, String name, Long id);

    List<IssueTypeDTO> queryByOrgId(Long organizationId);

    /**
     * 通过状态机方案id查询当前组织下的问题类型（包含对应的状态机）
     *
     * @param organizationId 组织id
     * @return 问题类型列表
     */
    List<IssueTypeDTO> queryIssueTypeByStateMachineSchemeId(Long organizationId, Long schemeId);

    /**
     * 消费组织创建事件生成组织初始化的五种issue类型
     *
     * @param organizationId organizationId
     */
    void initIssueTypeByConsumeCreateOrganization(Long organizationId);

    Map<Long, IssueTypeDTO> listIssueTypeMap(Long organizationId);

    Map<Long, Map<String, Long>> initIssueTypeData(Long organizationId, List<Long> orgIds);
}
