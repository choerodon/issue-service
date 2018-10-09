package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.IssueDTO;
import io.choerodon.issue.api.service.*;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class StateServiceImpl implements StateService {

    @Autowired
    private StateMachineFeignClient stateMachineClient;

    @Override
    public void handleState(Long organizationId, List<IssueDTO> issueDTOS) {
        List<StateDTO> stateDTOS = stateMachineClient.queryAllStatus(organizationId).getBody();
        Map<Long, String> map = stateDTOS.stream().collect(Collectors.toMap(StateDTO::getId, StateDTO::getName));
        for (IssueDTO issueDTO : issueDTOS) {
            Long stateId = issueDTO.getStatusId();
            if (stateId != null) {
                issueDTO.setStatusName(map.get(stateId));
            }
        }
    }
}
