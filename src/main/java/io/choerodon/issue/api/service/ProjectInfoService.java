package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.payload.ProjectEvent;

/**
 * @author shinan.chen
 * @date 2018/9/10
 */
public interface ProjectInfoService {

    void createProject(ProjectEvent projectEvent);

}
