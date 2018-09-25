package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueDTO;
import io.choerodon.issue.api.dto.SearchDTO;
import io.choerodon.issue.domain.Issue;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/9/3
 */
public interface IssueService extends BaseService<Issue> {

    IssueDTO queryById(Long projectId, Long issueId);

    IssueDTO create(Long projectId, IssueDTO issueDTO);

    IssueDTO updateIssue(IssueDTO issueDTO, List<String> updateFieldList);

    Page<IssueDTO> pageQuery(Long projectId, PageRequest pageRequest, SearchDTO searchDTO);

    /**
     * 获取状态机id
     * @param projectId 项目id
     * @param issueId 事件单id
     * @return
     */
    Long getStateMachineId(Long projectId, Long issueId);

//    List<IssueDTO> queryByOrgId(Long organizationId);
}
