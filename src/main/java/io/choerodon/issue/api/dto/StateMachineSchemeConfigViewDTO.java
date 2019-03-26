package io.choerodon.issue.api.dto;

import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineSchemeConfigViewDTO {
    @ApiModelProperty(value = "状态机DTO")
    private StateMachineDTO stateMachineDTO;
    @ApiModelProperty(value = "问题类型列表")
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

