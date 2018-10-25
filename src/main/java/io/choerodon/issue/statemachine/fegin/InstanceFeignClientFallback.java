package io.choerodon.issue.statemachine.fegin;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/9/25
 */
@Component
public class InstanceFeignClientFallback implements InstanceFeignClient {

    @Override
    public ResponseEntity<ExecuteResult> startInstance(Long organizationId, String serviceCode, Long stateMachineId, Long instanceId) {
        throw new CommonException("error.instanceFeign.startInstance");
    }

    @Override
    public ResponseEntity<ExecuteResult> executeTransform(Long organizationId, String serviceCode, Long stateMachineId, Long instanceId, Long currentStatusId, Long transformId) {
        throw new CommonException("error.instanceFeign.executeTransform");
    }
}
