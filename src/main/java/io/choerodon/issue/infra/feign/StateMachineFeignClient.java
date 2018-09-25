package io.choerodon.issue.infra.feign;

import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineTransfDTO;
import io.choerodon.issue.infra.feign.fallback.StateMachineFeignClientFallback;
import io.choerodon.core.domain.Page;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author peng.jiang
 */
@FeignClient(value = "state-machine-service",
        fallback = StateMachineFeignClientFallback.class)
@Component
public interface StateMachineFeignClient {

    /**
     * 获取状态机
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机Id
     * @return 状态机
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machine/get_stateMachine/{state_machine_id}", method = RequestMethod.GET)
    ResponseEntity<StateMachineDTO> getStateMachineById(@RequestParam(value = "organization_id") Long organizationId,
                                                        @RequestParam(value = "state_machine_id") Long stateMachineId);

    /**
     * 分页查询状态机列表
     *
     * @param organizationId
     * @param name
     * @param description
     * @param param
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machine", method = RequestMethod.GET)
    public ResponseEntity<Page<StateMachineDTO>> pagingQuery(@PathVariable("organization_id") Long organizationId,
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
    @RequestMapping(value = "/v1/organizations/{organization_id}/state_machine/{state_machine_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId,
                                          @PathVariable("state_machine_id") Long stateMachineId);


    /**
     * 创建状态机实例
     *
     * @param organizationId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instance/start_instance", method = RequestMethod.GET)
    public ResponseEntity<ExecuteResult> startInstance(@PathVariable("organization_id") Long organizationId,
                                                       @RequestParam("service_code") String serviceCode,
                                                       @RequestParam("state_machine_id") Long stateMachineId,
                                                       @RequestParam("instance_id") Long instanceId);

    /**
     * 显示事件单的转换
     *
     * @param organizationId
     * @param currentStateId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instance/transf_list", method = RequestMethod.GET)
    public ResponseEntity<List<StateMachineTransfDTO>> transfList(@PathVariable("organization_id") Long organizationId,
                                                                  @RequestParam("service_code") String serviceCode,
                                                                  @RequestParam("state_machine_id") Long stateMachineId,
                                                                  @RequestParam("instance_id") Long instanceId,
                                                                  @RequestParam("current_state_id") Long currentStateId);

    /**
     * 执行转换
     *
     * @param service_code   请求服务code
     * @param stateMachineId 状态机Id
     * @param instanceId     实例对象Id(cloopm-service: issueId)
     * @param currentStateId 当前状态Id
     * @param transfId       转换Id
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instance/execute_transf", method = RequestMethod.GET)
    public ResponseEntity<ExecuteResult> doTransf(@PathVariable("organization_id") Long organizationId,
                                                  @RequestParam("service_code") String serviceCode,
                                                  @RequestParam("state_machine_id") Long stateMachineId,
                                                  @RequestParam("instance_id") Long instanceId,
                                                  @RequestParam("current_state_id") Long currentStateId,
                                                  @RequestParam("transf_id") Long transfId);

    /**
     * 根据id获取状态
     *
     * @param organizationId
     * @param stateId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state/{state_id}", method = RequestMethod.GET)
    public ResponseEntity<StateDTO> getByStateId(@PathVariable("organization_id") Long organizationId, @PathVariable("state_id") Long stateId);

    /**
     * 根据id获取状态
     *
     * @param organizationId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/state/selectAll", method = RequestMethod.GET)
    public ResponseEntity<List<StateDTO>> selectAllStates(@PathVariable("organization_id") Long organizationId);

}
