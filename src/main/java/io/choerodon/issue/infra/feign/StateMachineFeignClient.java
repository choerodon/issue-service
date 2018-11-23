package io.choerodon.issue.infra.feign;

import io.choerodon.core.domain.Page;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineWithStatusDTO;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.issue.infra.feign.fallback.StateMachineFeignClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/25
 */
@FeignClient(value = "state-machine-service", fallback = StateMachineFeignClientFallback.class)
@Component
public interface StateMachineFeignClient {

    /**
     * 获取状态机
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机Id
     * @return 状态机
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machines/{state_machine_id}", method = RequestMethod.GET)
    ResponseEntity<StateMachineDTO> queryStateMachineById(@RequestParam(value = "organization_id") Long organizationId,
                                                          @RequestParam(value = "state_machine_id") Long stateMachineId);

    /**
     * 获取组织默认状态机
     *
     * @param organizationId 组织id
     * @return 状态机
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machines/default", method = RequestMethod.GET)
    ResponseEntity<StateMachineDTO> queryDefaultStateMachine(@RequestParam(value = "organization_id") Long organizationId);

    /**
     * 分页查询状态机列表
     *
     * @param organizationId
     * @param name
     * @param description
     * @param param
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machines", method = RequestMethod.GET)
    ResponseEntity<Page<StateMachineDTO>> pagingQuery(@PathVariable("organization_id") Long organizationId,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "size", required = false) Integer size,
                                                      @RequestParam(value = "sort", required = false) String[] sort,
                                                      @RequestParam(value = "name", required = false) String name,
                                                      @RequestParam(value = "description", required = false) String description,
                                                      @RequestParam(value = "param", required = false) String[] param);

    /**
     * 删除状态机
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机Id
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machines/{state_machine_id}", method = RequestMethod.DELETE)
    ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId,
                                   @PathVariable("state_machine_id") Long stateMachineId);

    /**
     * 根据id获取状态
     *
     * @param organizationId
     * @param statusId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/status/{status_id}", method = RequestMethod.GET)
    ResponseEntity<StatusDTO> queryStatusById(@PathVariable("organization_id") Long organizationId, @PathVariable("status_id") Long statusId);

    /**
     * 根据id获取状态
     *
     * @param organizationId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/status/query_all", method = RequestMethod.GET)
    ResponseEntity<List<StatusDTO>> queryAllStatus(@PathVariable("organization_id") Long organizationId);

    /**
     * 【初始化项目】创建项目时创建该项目的状态机，返回状态机id
     *
     * @param organizationId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machines/create_with_create_project", method = RequestMethod.POST)
    ResponseEntity<Long> createStateMachineWithCreateProject(@PathVariable("organization_id") Long organizationId,
                                                             @RequestParam("applyType") String applyType,
                                                             @RequestBody ProjectEvent projectEvent);

    /**
     * 显示事件单的转换
     *
     * @param organizationId
     * @param serviceCode
     * @param stateMachineId
     * @param instanceId
     * @param currentStatusId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instances/transform_list", method = RequestMethod.GET)
    ResponseEntity<List<TransformDTO>> transformList(@PathVariable("organization_id") Long organizationId,
                                                     @RequestParam("service_code") String serviceCode,
                                                     @RequestParam("state_machine_id") Long stateMachineId,
                                                     @RequestParam("instance_id") Long instanceId,
                                                     @RequestParam("current_status_id") Long currentStatusId);

    /**
     * 敏捷新增状态
     *
     * @param organizationId
     * @param stateMachineId
     * @param statusDTO
     * @return
     */
    @PostMapping(value = "/v1/organizations/{organization_id}/status/create_status_for_agile")
    ResponseEntity<StatusDTO> createStatusForAgile(@PathVariable("organization_id") Long organizationId,
                                                   @RequestParam("state_machine_id") Long stateMachineId,
                                                   @RequestBody StatusDTO statusDTO);

    /**
     * 查询状态机下的所有状态
     *
     * @param organizationId
     * @param stateMachineIds
     * @return
     */
    @PostMapping(value = "/v1/organizations/{organization_id}/status/query_by_state_machine_id")
    ResponseEntity<List<StatusDTO>> queryByStateMachineIds(@PathVariable("organization_id") Long organizationId,
                                                           @RequestBody @Valid List<Long> stateMachineIds);

    /**
     * 批量活跃状态机
     *
     * @param organizationId
     * @param stateMachineIds
     * @return
     */
    @PostMapping(value = "/v1/organizations/{organization_id}/state_machines/active_state_machines")
    ResponseEntity<Boolean> activeStateMachines(@PathVariable("organization_id") Long organizationId,
                                                @RequestBody List<Long> stateMachineIds);

    /**
     * 获取组织下所有状态机，包含状态
     *
     * @param organizationId
     * @return
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/state_machines/query_all_with_status")
    ResponseEntity<List<StateMachineWithStatusDTO>> queryAllWithStatus(@PathVariable("organization_id") Long organizationId);
}
