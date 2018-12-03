package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.api.dto.IssueTypeSearchDTO;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.domain.IssueTypeWithInfo;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
@Component
public interface IssueTypeMapper extends BaseMapper<IssueType> {
//    List<IssueType> fulltextSearch(@Param("issueType") IssueType issueType, @Param("param") String param);

    List<IssueType> queryBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);

    List<IssueType> queryByOrgId(@Param("organizationId") Long organizationId);

    List<Long> selectIssueTypeIds(@Param("organizationId") Long organizationId, @Param("issueTypeSearchDTO") IssueTypeSearchDTO issueTypeSearchDTO);

    List<IssueTypeWithInfo> queryIssueTypeList(@Param("organizationId") Long organizationId, @Param("issueTypeIds") List<Long> issueTypeIds);
}
