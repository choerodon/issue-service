package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.ProjectConfigDetailDTO;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */

@RestController
@RequestMapping("/v1/projects/{project_id}/project_configs")
public class ProjectConfigController {
    @Autowired
    ProjectConfigService projectConfigService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取项目配置方案信息")
    @GetMapping
    public ResponseEntity<ProjectConfigDetailDTO> queryById(@PathVariable("project_id") Long projectId) {
        return new ResponseEntity<>(projectConfigService.queryById(projectId), HttpStatus.OK);
    }
}
