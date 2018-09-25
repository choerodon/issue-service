package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.IssueReply;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */
@Component
public interface IssueReplyMapper extends BaseMapper<IssueReply> {
    List<IssueReply> fulltextSearch(@Param("issueReply") IssueReply issueReply, @Param("param") String param);
}
