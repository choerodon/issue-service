package io.choerodon.issue.api.controller;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.StateMachineDTO;
import io.choerodon.issue.api.dto.StateMachineListDTO;
import io.choerodon.issue.api.service.InitService;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.api.validator.StateMachineValidator;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/state_machine")
public class StateMachineController {

    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private StateMachineValidator stateMachineValidator;
    @Autowired
    private InitService initService;
    @Autowired
    private ProjectConfigService projectConfigService;


    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "分页查询状态机列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<StateMachineListDTO>> pagingQuery(@PathVariable("organization_id") Long organizationId,
                                                                     @ApiIgnore
                                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                     @RequestParam(required = false) String name,
                                                                     @RequestParam(required = false) String description,
                                                                     @RequestParam(required = false) String[] param) {
        return new ResponseEntity<>(stateMachineService.pageQuery(organizationId, pageRequest, name, description, param), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建状态机")
    @PostMapping
    public ResponseEntity<StateMachineDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody StateMachineDTO stateMachineDTO) {
        stateMachineValidator.createValidate(stateMachineDTO);
        return new ResponseEntity<>(stateMachineService.create(organizationId, stateMachineDTO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "更新状态机")
    @PutMapping(value = "/{state_machine_id}")
    public ResponseEntity<StateMachineDTO> update(@PathVariable("organization_id") Long organizationId,
                                                  @PathVariable("state_machine_id") Long stateMachineId,
                                                  @RequestBody StateMachineDTO stateMachineDTO) {
        stateMachineValidator.updateValidate(stateMachineDTO);
        return new ResponseEntity<>(stateMachineService.update(organizationId, stateMachineId, stateMachineDTO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "发布状态机")
    @GetMapping(value = "/deploy/{state_machine_id}")
    public ResponseEntity<Boolean> deploy(@PathVariable("organization_id") Long organizationId,
                                          @PathVariable("state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(stateMachineService.deploy(organizationId, stateMachineId, true), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取状态机及配置（草稿/新建）")
    @GetMapping(value = "/with_config_draft/{state_machine_id}")
    public ResponseEntity<StateMachineDTO> queryStateMachineWithConfigDraftById(@PathVariable("organization_id") Long organizationId,
                                                                                @PathVariable("state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(stateMachineService.queryStateMachineWithConfigById(organizationId, stateMachineId, true), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取状态机原件及配置（活跃）")
    @GetMapping(value = "/with_config_deploy/{state_machine_id}")
    public ResponseEntity<StateMachineDTO> queryStateMachineWithConfigOriginById(@PathVariable("organization_id") Long organizationId,
                                                                                 @PathVariable("state_machine_id") Long stateMachineId) {

        return new ResponseEntity<>(stateMachineService.queryStateMachineWithConfigById(organizationId, stateMachineId, false), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取状态机（无配置）")
    @GetMapping(value = "/{state_machine_id}")
    public ResponseEntity<StateMachineDTO> queryStateMachineById(@PathVariable("organization_id") Long organizationId,
                                                                 @PathVariable("state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(stateMachineService.queryStateMachineById(organizationId, stateMachineId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除草稿")
    @DeleteMapping(value = "/delete_draft/{state_machine_id}")
    public ResponseEntity<StateMachineDTO> deleteDraft(@PathVariable("organization_id") Long organizationId,
                                                       @PathVariable("state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(stateMachineService.deleteDraft(organizationId, stateMachineId), HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除状态机")
    @DeleteMapping(value = "/{state_machine_id}")
    public ResponseEntity delete(@PathVariable("organization_id") Long organizationId,
                                 @PathVariable("state_machine_id") Long stateMachineId) {
        stateMachineService.delete(organizationId, stateMachineId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "校验状态机名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name) {
        return Optional.ofNullable(stateMachineService.checkName(organizationId, name))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.stateMachineName.check"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取组织下所有状态机")
    @GetMapping(value = "/query_all")
    public ResponseEntity<List<StateMachineDTO>> queryAll(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(stateMachineService.queryAll(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "【内部调用】查询状态机关联的项目id列表的Map")
    @GetMapping(value = "/query_project_ids_map")
    public ResponseEntity<Map<String, List<Long>>> queryProjectIdsMap(@PathVariable("organization_id") Long organizationId,
                                                                      @RequestParam("stateMachineId") Long stateMachineId) {
        return Optional.ofNullable(projectConfigService.queryProjectIdsMap(organizationId, stateMachineId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.queryProjectIdsMap.get"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "【内部调用】状态机删除节点的校验，是否可以直接删除")
    @GetMapping(value = "/check_delete_node")
    public ResponseEntity<Map<String, Object>> checkDeleteNode(@PathVariable("organization_id") Long organizationId,
                                                               @RequestParam("stateMachineId") Long stateMachineId,
                                                               @RequestParam("statusId") Long statusId) {
        return Optional.ofNullable(stateMachineService.checkDeleteNode(organizationId, stateMachineId, statusId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.checkDeleteNode.get"));
    }
}
