package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.IssueType;
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
    List<IssueType> fulltextSearch(@Param("issueType") IssueType issueType, @Param("param") String param);

    List<IssueType> queryBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);

    /**
     * 查询组织下未绑定到该方案的问题类型
     * @param organizationId 组织id
     * @param schemeId 方案id
     * @return 问题类型列表
     */
    List<IssueType> queryIssueType(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);
}
