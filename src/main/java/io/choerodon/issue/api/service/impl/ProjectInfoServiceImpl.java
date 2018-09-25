package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.api.service.*;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfoServiceImpl.class);

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public void createProject(ProjectEvent projectEvent) {
        ProjectInfo info = new ProjectInfo();
        info.setIssueMaxNum(0L);
        info.setProjectCode(projectEvent.getProjectCode());
        info.setProjectId(projectEvent.getProjectId());
        projectInfoMapper.insert(info);
    }
}
