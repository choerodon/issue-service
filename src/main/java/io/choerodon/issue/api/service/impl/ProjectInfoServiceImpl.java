package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.service.ProjectInfoService;
import io.choerodon.issue.domain.ProjectInfo;
import io.choerodon.issue.infra.mapper.ProjectInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @Date 2018/9/10
 */
@Component
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
