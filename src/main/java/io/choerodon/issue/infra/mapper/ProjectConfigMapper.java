package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/4
 */
@Component
public interface ProjectConfigMapper extends BaseMapper<ProjectConfig> {
    List<ProjectConfig> queryByProjectId(@Param("projectId") Long projectId);

    ProjectConfig queryBySchemeTypeAndApplyType(@Param("projectId") Long projectId, @Param("schemeType") String schemeType, @Param("applyType") String applyType);

    List<ProjectConfig> queryBySchemeIds(@Param("schemeIds") List<Long> schemeIds, @Param("schemeType") String schemeType);
}
