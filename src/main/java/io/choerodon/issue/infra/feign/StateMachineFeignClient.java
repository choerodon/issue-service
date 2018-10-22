package io.choerodon.issue.infra.feign;

import io.choerodon.core.domain.Page;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import io.choerodon.issue.infra.feign.fallback.StateMachineFeignClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    ResponseEntity<StateDTO> queryStatusById(@PathVariable("organization_id") Long organizationId, @PathVariable("status_id") Long statusId);

    /**
     * 根据id获取状态
     *
     * @param organizationId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/status/query_all", method = RequestMethod.GET)
    ResponseEntity<List<StateDTO>> queryAllStatus(@PathVariable("organization_id") Long organizationId);
}
