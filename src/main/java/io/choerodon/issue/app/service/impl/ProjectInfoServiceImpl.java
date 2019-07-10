package io.choerodon.issue.app.service.impl;

import io.choerodon.issue.app.service.ProjectInfoService;
import io.choerodon.issue.infra.dto.ProjectInfo;
import io.choerodon.issue.infra.mapper.ProjectInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author shinan.chen
 * @Date 2018/9/10
 */
@Service
@RefreshScope
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public void createProject(Long projectId, String projectCode) {
        ProjectInfo info = new ProjectInfo();
        info.setProjectCode(projectCode);
        info.setProjectId(projectId);
        //保证幂等性
        if (projectInfoMapper.select(info).isEmpty()) {
            info.setIssueMaxNum(0L);
            projectInfoMapper.insert(info);
        }
    }
}
