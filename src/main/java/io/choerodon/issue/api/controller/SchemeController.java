package io.choerodon.issue.api.controller;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.IssueTypeWithStateMachineIdDTO;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
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
    public ResponseEntity<List<IssueTypeDTO>> queryIssueTypesByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryIssueTypesByProjectId(projectId, applyType), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目的问题类型列表，带对应的状态机id")
    @GetMapping(value = "/query_issue_types_with_sm_id")
    public ResponseEntity<List<IssueTypeWithStateMachineIdDTO>> queryIssueTypesWithStateMachineIdByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryIssueTypesWithStateMachineIdByProjectId(projectId, applyType), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目下某个问题类型拥有的转换（包含可以转换到的状态）")
    @GetMapping(value = "/query_transforms")
    public ResponseEntity<List<TransformDTO>> queryTransformsByProjectId(@PathVariable("project_id") Long projectId,
                                                                         @RequestParam("current_status_id") Long currentStatusId,
                                                                         @RequestParam("issue_id") Long issueId,
                                                                         @RequestParam("issue_type_id") Long issueTypeId,
                                                                         @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryTransformsByProjectId(projectId, currentStatusId, issueId, issueTypeId, applyType), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目下某个问题类型的所有状态")
    @GetMapping(value = "/query_status_by_issue_type_id")
    public ResponseEntity<List<StatusDTO>> queryStatusByIssueTypeId(@PathVariable("project_id") Long projectId,
                                                                    @RequestParam("issue_type_id") Long issueTypeId,
                                                                    @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryStatusByIssueTypeId(projectId, issueTypeId, applyType), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目下的所有状态")
    @GetMapping(value = "/query_status_by_project_id")
    public ResponseEntity<List<StatusDTO>> queryStatusByProjectId(@PathVariable("project_id") Long projectId,
                                                                  @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryStatusByProjectId(projectId, applyType), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询项目的问题类型对应的状态机id")
    @GetMapping(value = "/query_state_machine_id")
    public ResponseEntity<Long> queryStateMachineId(@PathVariable("project_id") Long projectId,
                                                    @RequestParam("apply_type") String applyType,
                                                    @RequestParam("issue_type_id") Long issueTypeId) {
        return new ResponseEntity<>(projectConfigService.queryStateMachineId(projectId, applyType, issueTypeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "【敏捷】新增状态")
    @PostMapping(value = "/create_status_for_agile")
    public ResponseEntity<StatusDTO> createStatusForAgile(@PathVariable("project_id") Long projectId,
                                                          @RequestBody StatusDTO statusDTO) {
        return new ResponseEntity<>(projectConfigService.createStatusForAgile(projectId, statusDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "【敏捷】校验是否能新增状态")
    @GetMapping(value = "/check_create_status_for_agile")
    public ResponseEntity<Boolean> checkCreateStatusForAgile(@PathVariable("project_id") Long projectId) {
        return new ResponseEntity<>((Boolean) projectConfigService.checkCreateStatusForAgile(projectId).get("flag"), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "【敏捷】校验是否能删除状态")
    @GetMapping(value = "/check_delete_status_for_agile")
    public ResponseEntity<Boolean> checkDeleteStatusForAgile(@PathVariable("project_id") Long projectId,
                                                             @RequestParam("status_id") Long statusId) {
        return new ResponseEntity<>(projectConfigService.checkDeleteStatusForAgile(projectId, statusId), HttpStatus.OK);
    }
}
