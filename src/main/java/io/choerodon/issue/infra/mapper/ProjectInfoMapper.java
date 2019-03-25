package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.ProjectInfo;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/9/10
 */
@Component
public interface ProjectInfoMapper extends BaseMapper<ProjectInfo> {
    ProjectInfo queryByProjectId(@Param("projectId") Long projectId);

    int updateIssueMaxNum(@Param("projectId") Long projectId);

//    void demoProjectClean(@Param("projectId") Long projectId);
}
