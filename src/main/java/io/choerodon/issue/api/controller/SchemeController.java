package io.choerodon.issue.api.controller;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.IssueTypeWithStateMachineIdDTO;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 根据项目id获取对应数据
 *
 * @author shinan.chen
 * @date 2018/10/24
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/schemes")
public class SchemeController extends BaseController {

    @Autowired
    private ProjectConfigService projectConfigService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目的问题类型列表")
    @GetMapping(value = "/query_issue_types")
    public ResponseEntity<List<IssueTypeDTO>> queryIssueTypesByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("scheme_type") String schemeType) {
        return new ResponseEntity<>(projectConfigService.queryIssueTypesByProjectId(projectId, schemeType), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目的问题类型列表，带对应的状态机id")
    @GetMapping(value = "/query_issue_types")
    public ResponseEntity<List<IssueTypeWithStateMachineIdDTO>> queryIssueTypesWithStateMachineIdByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("scheme_type") String schemeType) {
        return new ResponseEntity<>(projectConfigService.queryIssueTypesWithStateMachineIdByProjectId(projectId, schemeType), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询某个问题类型拥有的转换（包含可以转换到的状态）")
    @GetMapping(value = "/query_transforms")
    public ResponseEntity<List<TransformDTO>> queryTransformsByProjectId(@PathVariable("project_id") Long projectId,
                                                                         @RequestParam("current_status_id") Long currentStatusId,
                                                                         @RequestParam("issue_id") Long issueId,
                                                                         @RequestParam("issue_type_id") Long issueTypeId,
                                                                         @RequestParam("scheme_type") String schemeType) {
        return new ResponseEntity<>(projectConfigService.queryTransformsByProjectId(projectId, currentStatusId, issueId, issueTypeId, schemeType), HttpStatus.OK);
    }

}
