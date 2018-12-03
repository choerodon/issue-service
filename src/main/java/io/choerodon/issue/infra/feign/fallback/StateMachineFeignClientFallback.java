package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineWithStatusDTO;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
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
    public ResponseEntity<StateMachineDTO> queryDefaultStateMachine(Long organizationId) {
        throw new CommonException("error.stateMachineFeignClient.queryDefaultStateMachine");
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
    public ResponseEntity<StatusDTO> queryStatusById(Long organizationId, Long statusId) {
        throw new CommonException("error.stateMachineFeignClient.queryStatusById");
    }

    @Override
    public ResponseEntity<List<StatusDTO>> queryAllStatus(Long organizationId) {
        throw new CommonException("error.stateMachineFeignClient.queryAllStatus");
    }

    @Override
    public ResponseEntity<Long> createStateMachineWithCreateProject(Long organizationId, String applyType, ProjectEvent projectEvent) {
        throw new CommonException("error.stateMachineFeignClient.createStateMachineWithCreateProject");
    }

    @Override
    public ResponseEntity<List<TransformDTO>> transformList(Long organizationId, String serviceCode, Long stateMachineId, Long instanceId, Long currentStatusId) {
        throw new CommonException("error.stateMachineFeignClient.transformList");
    }

    @Override
    public ResponseEntity<StatusDTO> createStatusForAgile(Long organizationId, Long stateMachineId, StatusDTO statusDTO) {
        throw new CommonException("error.stateMachineFeignClient.createStatusForAgile");
    }

    @Override
    public ResponseEntity<List<StatusDTO>> queryByStateMachineIds(Long organizationId, List<Long> stateMachineIds) {
        throw new CommonException("error.stateMachineFeignClient.queryByStateMachineIds");
    }

    @Override
    public ResponseEntity<Boolean> activeStateMachines(Long organizationId, List<Long> stateMachineIds) {
        throw new CommonException("error.stateMachineFeignClient.activeStateMachines");
    }

    @Override
    public ResponseEntity<Boolean> notActiveStateMachines(Long organizationId, List<Long> stateMachineIds) {
        throw new CommonException("error.stateMachineFeignClient.notActiveStateMachines");
    }

    @Override
    public ResponseEntity<List<StateMachineWithStatusDTO>> queryAllWithStatus(Long organizationId) {
        throw new CommonException("error.stateMachineFeignClient.queryAllWithStatus");
    }

    @Override
    public ResponseEntity<List<StateMachineDTO>> queryByOrgId(Long organizationId) {
        throw new CommonException("error.stateMachineFeignClient.queryByOrgId");
    }

    @Override
    public ResponseEntity removeStateMachineNode(Long organizationId, Long stateMachineId, Long statusId) {
        throw new CommonException("error.StateMachineNode.remove");
    }
}
