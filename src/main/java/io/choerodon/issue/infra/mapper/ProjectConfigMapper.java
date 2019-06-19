package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/4
 */
@Component
public interface ProjectConfigMapper extends Mapper<ProjectConfig> {
    List<ProjectConfig> queryByProjectId(@Param("projectId") Long projectId);

    ProjectConfig queryBySchemeTypeAndApplyType(@Param("projectId") Long projectId, @Param("schemeType") String schemeType, @Param("applyType") String applyType);

    List<ProjectConfig> queryBySchemeIds(@Param("schemeIds") List<Long> schemeIds, @Param("schemeType") String schemeType);
    /**
     * 通过方案ids查询出关联的项目（项目关联的状态机方案）
     *
     * @param schemeIds
     * @param schemeType
     * @return
     */
    List<ProjectConfig> handleRemoveStatus(@Param("schemeIds") List<Long> schemeIds, @Param("schemeType") String schemeType);

    List<ProjectConfig> queryByProjectIds(@Param("projectIds") List<Long> projectIds);

    List<ProjectConfig> queryConfigsBySchemeId(@Param("schemeType") String schemeType, @Param("schemeId") Long schemeId);
}
