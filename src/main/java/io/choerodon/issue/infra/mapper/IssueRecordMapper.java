package io.choerodon.issue.infra.mapper;

import feign.Param;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * @author peng.jiang
 * @date 2018/9/4
 */
public interface IssueRecordMapper extends BaseMapper<IssueRecord> {

    List<IssueRecord> queryGroup(@Param("issueRecordId") Long issueRecordId);
}
