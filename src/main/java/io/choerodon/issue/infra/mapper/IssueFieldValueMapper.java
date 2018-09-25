package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.api.dto.IssueFieldValueSearchDTO;
import io.choerodon.issue.domain.IssueFieldValue;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/4
 */
@Component
public interface IssueFieldValueMapper extends BaseMapper<IssueFieldValue> {
    List<IssueFieldValue> queryByIssueId(@Param("issueId") Long issueId);

    List<Long> queryIssueIdsBySearch(@Param("projectId") Long projectId, @Param("fieldValues") List<IssueFieldValueSearchDTO> fieldValues);
}
