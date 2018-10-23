package io.choerodon.issue.statemachine.fegin;

import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
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
        fallback = InstanceFeignClientFallback.class)
@Component
public interface InstanceFeignClient {

    /**
     * 创建状态机实例
     *
     * @param organizationId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instances/start_instance", method = RequestMethod.GET)
    ResponseEntity<ExecuteResult> startInstance(@PathVariable("organization_id") Long organizationId,
                                                @RequestParam("service_code") String serviceCode,
                                                @RequestParam("state_machine_id") Long stateMachineId,
                                                @RequestParam("instance_id") Long instanceId);

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
    ResponseEntity<List<TransformInfo>> transformList(@PathVariable("organization_id") Long organizationId,
                                                      @RequestParam("service_code") String serviceCode,
                                                      @RequestParam("state_machine_id") Long stateMachineId,
                                                      @RequestParam("instance_id") Long instanceId,
                                                      @RequestParam("current_status_id") Long currentStatusId);

    /**
     * 执行转换
     *
     * @param organizationId
     * @param serviceCode
     * @param stateMachineId
     * @param instanceId
     * @param currentStatusId
     * @param transformId
     * @return
     */
    @RequestMapping(value = "/v1/organizations/{organization_id}/instances/execute_transform", method = RequestMethod.GET)
    ResponseEntity<ExecuteResult> executeTransform(@PathVariable("organization_id") Long organizationId,
                                                   @RequestParam("service_code") String serviceCode,
                                                   @RequestParam("state_machine_id") Long stateMachineId,
                                                   @RequestParam("instance_id") Long instanceId,
                                                   @RequestParam("current_status_id") Long currentStatusId,
                                                   @RequestParam("transform_id") Long transformId);
}
