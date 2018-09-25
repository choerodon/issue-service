package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueReplyDTO;
import io.choerodon.issue.domain.IssueReply;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */
public interface IssueReplyService extends BaseService<IssueReply> {
    List<IssueReplyDTO> listQuery(IssueReplyDTO issueReplyDTO, String s);

    IssueReplyDTO create(Long projectId, IssueReplyDTO issueReplyDTO);

    IssueReplyDTO update(IssueReplyDTO issueReplyDTO);

    IssueReplyDTO queryByIssueReplyId(Long projectId, Long id);

    Boolean delete(Long projectId, Long issueReplyId);

    Map<String, Object> checkDelete(Long projectId, Long issueReplyId);
}
