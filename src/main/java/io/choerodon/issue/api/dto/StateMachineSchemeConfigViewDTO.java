package io.choerodon.issue.api.dto;

import io.choerodon.issue.infra.feign.dto.StateMachineDTO;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineSchemeConfigViewDTO {

    private StateMachineDTO stateMachineDTO;
    private List<IssueTypeDTO> issueTypeDTOs;

    public StateMachineDTO getStateMachineDTO() {
        return stateMachineDTO;
    }

    public void setStateMachineDTO(StateMachineDTO stateMachineDTO) {
        this.stateMachineDTO = stateMachineDTO;
    }

    public List<IssueTypeDTO> getIssueTypeDTOs() {
        return issueTypeDTOs;
    }

    public void setIssueTypeDTOs(List<IssueTypeDTO> issueTypeDTOs) {
        this.issueTypeDTOs = issueTypeDTOs;
    }
}

