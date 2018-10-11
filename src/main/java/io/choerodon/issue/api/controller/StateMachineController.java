package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/state_machine")
public class StateMachineController {

    @Autowired
    private StateMachineService stateMachineService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询状态机列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<StateMachineDTO>> pagingQuery(@PathVariable("organization_id") Long organizationId,
                                                             @RequestParam(required = false) Integer page,
                                                             @RequestParam(required = false) Integer size,
                                                             @RequestParam(required = false) String[] sort,
                                                             @RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String description,
                                                             @RequestParam(required = false) String[] param) {
        return stateMachineService.pageQuery(organizationId, page, size, sort, name, description, param);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除状态机")
    @DeleteMapping(value = "/{state_machine_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("state_machine_id") Long stateMachineId) {
        return stateMachineService.delete(organizationId, stateMachineId);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "状态机执行转换")
    @GetMapping(value = "/do_transf")
    public ResponseEntity<ExecuteResult> doTransf(@PathVariable("organization_id") Long organizationId,
                                                  @RequestParam(value = "project_id") Long projectId,
                                                  @RequestParam(value = "issue_id") Long issueId,
                                                  @RequestParam(value = "transf_id") Long transfId) {
        return stateMachineService.doTransf(organizationId, projectId, issueId, transfId);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "显示转换")
    @GetMapping(value = "/transf_list")
    public ResponseEntity<List<TransformInfo>> transfList(@PathVariable("organization_id") Long organizationId,
                                                          @RequestParam(value = "project_id") Long projectId,
                                                          @RequestParam(value = "issue_id") Long issueId) {
        return stateMachineService.transfList(organizationId, projectId, issueId);
    }


    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "条件过滤")
    @PostMapping(value = "/config_filter")
    public ResponseEntity<List<TransformInfo>> configFilter(@PathVariable("organization_id") Long organizationId,
                                                            @RequestParam(value = "instance_id") Long instanceId,
                                                            @RequestBody List<TransformInfo> transfDTOS) {
        return new ResponseEntity<>(stateMachineService.conditionFilter(organizationId, instanceId, transfDTOS), HttpStatus.OK);
    }



}
