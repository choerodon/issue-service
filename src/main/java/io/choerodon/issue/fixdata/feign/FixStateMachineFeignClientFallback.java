package io.choerodon.issue.fixdata.feign;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.fixdata.dto.StatusForMoveDataDO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/25
 */
@Component
public class FixStateMachineFeignClientFallback implements FixStateMachineFeignClient {
    @Override
    public ResponseEntity<Map<String, Long>> createAGStateMachineAndTEStateMachine(Long organizationId, String projectCode, List<String> statuses) {
        throw new CommonException("error.fixStateMachineFeignClient.createAGStateMachineAndTEStateMachine");
    }

    @Override
    public void createStatus(List<StatusForMoveDataDO> statusForMoveDataDOList) {
        throw new CommonException("error.fixStateMachineFeignClient.createStatus");
    }
}
