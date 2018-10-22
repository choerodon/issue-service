package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/25
 */
@Component
public class StateMachineFeignClientFallback implements StateMachineFeignClient {

    @Override
    public ResponseEntity<StateMachineDTO> queryStateMachineById(Long organizationId, Long stateMachineId) {
        throw new CommonException("error.stateMachineFeignClient.queryStateMachineById");
    }

    @Override
    public ResponseEntity<Page<StateMachineDTO>> pagingQuery(Long organizationId, Integer page, Integer size, String[] sort, String name, String description, String[] param) {
        throw new CommonException("error.stateMachineFeignClient.pagingQuery");
    }

    @Override
    public ResponseEntity<Boolean> delete(Long organizationId, Long stateMachineId) {
        throw new CommonException("error.stateMachineFeignClient.delete");
    }

    @Override
    public ResponseEntity<StateDTO> queryStatusById(Long organizationId, Long statusId) {
        throw new CommonException("error.stateMachineFeignClient.queryStatusById");
    }

    @Override
    public ResponseEntity<List<StateDTO>> queryAllStatus(Long organizationId) {
        throw new CommonException("error.stateMachineFeignClient.queryAllStatus");
    }

}
