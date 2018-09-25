package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/9/4
 */
@Component
public interface ProjectConfigMapper extends BaseMapper<ProjectConfig> {
    ProjectConfig queryByProjectId(@Param("projectId") Long projectId);
}
