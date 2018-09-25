package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.Issue;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/9/3
 */
@Component
public interface IssueMapper extends BaseMapper<Issue> {
    List<Issue> queryIssuesByArgs(@Param("projectId") Long projectId, @Param("searchArgs") Map<String, Object> searchArgs, @Param("searchArgsIds") Map<String, Object> searchArgsIds, @Param("param") String param, @Param("paramIds") Map<String, Object> paramIds);

    Issue queryById(@Param("projectId") Long projectId, @Param("issueId") Long issueId);
}
