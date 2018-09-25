package io.choerodon.issue.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/demo")
public class DemoController {

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("测试接口")
    @GetMapping
    public ResponseEntity test(@ApiParam(value = "项目id", required = true)
                               @PathVariable(name = "project_id") Long projectId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
