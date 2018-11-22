package io.choerodon.issue.infra.feign;

import io.choerodon.issue.api.dto.payload.StateMachineSchemeDeployCheckIssue;
import io.choerodon.issue.infra.feign.fallback.AgileFeignClientFallback;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2018/11/21
 */
@FeignClient(value = "agile-service",
        fallback = AgileFeignClientFallback.class)
@Component
public interface AgileFeignClient {
    /**
     * 校验是否可以删除状态机的节点
     *
     * @param organizationId
     * @param statusId
     * @param issueTypeIdsMap
     * @return
     */
    @PostMapping("/v1/organizations/{organization_id}/state_machine/check_delete_node")
    ResponseEntity<Map<String, Object>> checkDeleteNode(@ApiParam(value = "组织id", required = true)
                                                        @PathVariable(name = "organization_id") Long organizationId,
                                                        @ApiParam(value = "状态id", required = true)
                                                        @RequestParam(value = "status_id") Long statusId,
                                                        @RequestBody Map<Long, List<Long>> issueTypeIdsMap);

    /**
     * 查询状态机方案变更后对issue的影响
     *
     * @param organizationId
     * @param deployCheckIssue
     * @return
     */
    @PostMapping("/v1/organizations/{organization_id}/state_machine/check_state_machine_scheme_change")
    ResponseEntity<Map<Long, Long>> checkStateMachineSchemeChange(@ApiParam(value = "组织id", required = true)
                                                                  @PathVariable(name = "organization_id") Long organizationId,
                                                                  @RequestBody StateMachineSchemeDeployCheckIssue deployCheckIssue);
}
