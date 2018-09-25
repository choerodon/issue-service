package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineTransfDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class StateMachineFeignClientFallback implements StateMachineFeignClient {

    @Override
    public ResponseEntity<StateMachineDTO> getStateMachineById(Long organizationId, Long stateMachineId) {
        throw new CommonException("error.getStateMachineById");
    }

    @Override
    public ResponseEntity<Page<StateMachineDTO>> pagingQuery(Long organizationId, Integer page, Integer size, String[] sort, String name, String description, String[] param) {
        throw new CommonException("error.pagingQuery");
    }

    @Override
    public ResponseEntity<Boolean> delete(Long organizationId, Long stateMachineId) {
        throw new CommonException("error.delete");
    }

    @Override
    public ResponseEntity<ExecuteResult> startInstance(Long organizationId, String serverCode, Long stateMachineId, Long instanceId) {
        throw new CommonException("error.startInstance");
    }

    @Override
    public ResponseEntity<List<StateMachineTransfDTO>> transfList(Long organizationId, String serviceCode,
                                                                  Long stateMachineId, Long instanceId,
                                                                  Long currentStateId) {
        throw new CommonException("error.transfList");
    }

    @Override
    public ResponseEntity<ExecuteResult> doTransf(Long organizationId, String serviceCode, Long stateMachineId,
                                                  Long instanceId, Long currentStateId, Long transfId) {
        throw new CommonException("error.doTransf");
    }

    @Override
    public ResponseEntity<StateDTO> getByStateId(Long organizationId, Long stateId) {
        throw new CommonException("error.getByStateId");
    }

    @Override
    public ResponseEntity<List<StateDTO>> selectAllStates(Long organizationId) {
        throw new CommonException("error.selectAllStates");
    }
}
