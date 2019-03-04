package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.payload.StateMachineSchemeDeployCheckIssue;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2018/11/21
 */
@Component
public class AgileFeignClientFallback implements AgileFeignClient {
    @Override
    public ResponseEntity<Map<String, Object>> checkDeleteNode(Long organizationId, Long statusId, List<ProjectConfig> projectConfigs) {
        throw new CommonException("error.agileFeignClient.checkDeleteNode");
    }

    @Override
    public ResponseEntity<Map<Long, Long>> checkStateMachineSchemeChange(Long organizationId, StateMachineSchemeDeployCheckIssue deployCheckIssue) {
        throw new CommonException("error.agileFeignClient.checkStateMachineSchemeChange");
    }

    @Override
    public ResponseEntity<Long> checkPriorityDelete(Long organizationId, Long priorityId, List<Long> projectIds) {
        throw new CommonException("error.agileFeignClient.checkPriorityDelete");
    }

    @Override
    public ResponseEntity batchChangeIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds) {
        throw new CommonException("error.agileFeignClient.batchChangeIssuePriority");
    }
}


