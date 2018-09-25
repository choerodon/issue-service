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
 * @author jiameng.cao
 * @date 2018/9/10
 */

@RestController
@RequestMapping("/v1/projects/{project_id}")
public class ProjectConfigController {
    @Autowired
    ProjectConfigService projectConfigService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取问题类型方案")
    @GetMapping(value = "/issue_type_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> issueTypeSchemeQuery(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.queryIssueTypeByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取状态机方案")
    @GetMapping(value = "/state_machine_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> stateMachineSchemeQuery(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();

        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.queryStateMachineByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取问题类型页面方案")
    @GetMapping(value = "/page_issue_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> pageIssueSchemeQuery(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.queryPageIssueByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取字段配置方案")
    @GetMapping(value = "/field_config_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> fieldConfigSchemeQuery(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.queryFieldConfigByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "更新字段配置方案")
    @PutMapping(value = "/field_config_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> updateFieldConfigScheme(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.updateFieldConfigByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "更新问题类型方案")
    @PutMapping(value = "/issue_type_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> updateIssueTypeScheme(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.updateIssueTypeByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "更新状态机方案")
    @PutMapping(value = "/state_machine_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> updateStateMachineScheme(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.updateStateMachineByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "更新问题类型页面方案")
    @PutMapping(value = "/page_issue_scheme")
    public ResponseEntity<ProjectConfigDetailDTO> updatePageIssueScheme(@PathVariable("project_id") Long projectId) {

        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);

        return new ResponseEntity<>(projectConfigService.updatePageIssueByProjectId(projectConfigDetailDTO), HttpStatus.OK);
    }

}
