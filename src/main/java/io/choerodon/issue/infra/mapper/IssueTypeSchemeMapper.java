package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.IssueTypeScheme;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
@Component
public interface IssueTypeSchemeMapper extends BaseMapper<IssueTypeScheme> {
    List<IssueTypeScheme> fulltextSearch(@Param("issueTypeScheme") IssueTypeScheme issueTypeScheme, @Param("param") String param);
}
