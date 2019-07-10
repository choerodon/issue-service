package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.infra.dto.ProjectInfo;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/9/10
 */
@Component
public interface ProjectInfoMapper extends Mapper<ProjectInfo> {
    ProjectInfo queryByProjectId(@Param("projectId") Long projectId);

    int updateIssueMaxNum(@Param("projectId") Long projectId);
}
