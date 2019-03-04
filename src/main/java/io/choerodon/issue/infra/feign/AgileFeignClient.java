package io.choerodon.issue.infra.feign;

import io.choerodon.issue.api.dto.payload.StateMachineSchemeDeployCheckIssue;
import io.choerodon.issue.domain.ProjectConfig;
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
     * @param projectConfigs
     * @return
     */
    @PostMapping("/v1/organizations/{organization_id}/state_machine/check_delete_node")
    ResponseEntity<Map<String, Object>> checkDeleteNode(@ApiParam(value = "组织id", required = true)
                                                        @PathVariable(name = "organization_id") Long organizationId,
                                                        @ApiParam(value = "状态id", required = true)
                                                        @RequestParam(value = "status_id") Long statusId,
                                                        @RequestBody List<ProjectConfig> projectConfigs);

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

    /**
     * 校验删除优先级
     *
     * @param organizationId
     * @param priorityId
     * @param projectIds
     * @return
     */
    @PostMapping("/v1/organizations/{organization_id}/issues/check_priority_delete")
    ResponseEntity<Long> checkPriorityDelete(@ApiParam(value = "组织id", required = true)
                                             @PathVariable(name = "organization_id") Long organizationId,
                                             @ApiParam(value = "priorityId", required = true)
                                             @RequestParam(value = "priority_id") Long priorityId,
                                             @RequestBody List<Long> projectIds);

    /**
     * 批量更改Issue的优先级
     *
     * @param organizationId
     * @param priorityId
     * @param changePriorityId
     * @param projectIds
     * @return
     */
    @PostMapping("/v1/organizations/{organization_id}/issues/batch_change_issue_priority")
    ResponseEntity batchChangeIssuePriority(@ApiParam(value = "组织id", required = true)
                                            @PathVariable(name = "organization_id") Long organizationId,
                                            @ApiParam(value = "priorityId", required = true)
                                            @RequestParam(value = "priority_id") Long priorityId,
                                            @ApiParam(value = "changePriorityId", required = true)
                                            @RequestParam(value = "change_priority_id") Long changePriorityId,
                                            @RequestParam(value = "user_id") Long userId,
                                            @RequestBody List<Long> projectIds);

}
