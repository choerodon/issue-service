package io.choerodon.issue.api.controller;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.service.ProjectUserService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author peng.jiang
 */
@RestController
@RequestMapping(value = "/v1/projects/{organization_id}")
public class ProjectUserController extends BaseController {

    @Autowired
    ProjectUserService projectUserService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "创建用户")
    @PostMapping("/{project_id}/users")
    public ResponseEntity<UserDTO> create(@PathVariable(name = "organization_id") Long organizationId,
                                          @PathVariable(name = "project_id") Long projectId,
                                          @RequestBody UserDTO userDTO) {
        return projectUserService.createUser(organizationId, projectId, userDTO);
    }

}
