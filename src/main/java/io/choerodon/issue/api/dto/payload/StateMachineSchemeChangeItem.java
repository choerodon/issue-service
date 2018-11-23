package io.choerodon.issue.api.dto.payload;

import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineWithStatusDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/22
 */
public class StateMachineSchemeChangeItem {
    private Long issueTypeId;
    private Long issueCount;
    private Long oldStateMachineId;
    private Long newStateMachineId;
    private IssueTypeDTO issueTypeDTO;
    private StateMachineWithStatusDTO oldStateMachine;
    private StateMachineWithStatusDTO newStateMachine;
    private List<StateMachineSchemeStatusChangeItem> stateMachineSchemeStatusChangeItems;

    public StateMachineSchemeChangeItem() {
    }

    public StateMachineSchemeChangeItem(Long issueTypeId, Long oldStateMachineId, Long newStateMachineId) {
        this.issueTypeId = issueTypeId;
        this.oldStateMachineId = oldStateMachineId;
        this.newStateMachineId = newStateMachineId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getOldStateMachineId() {
        return oldStateMachineId;
    }

    public void setOldStateMachineId(Long oldStateMachineId) {
        this.oldStateMachineId = oldStateMachineId;
    }

    public Long getNewStateMachineId() {
        return newStateMachineId;
    }

    public void setNewStateMachineId(Long newStateMachineId) {
        this.newStateMachineId = newStateMachineId;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public Long getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Long issueCount) {
        this.issueCount = issueCount;
    }

    public StateMachineWithStatusDTO getOldStateMachine() {
        return oldStateMachine;
    }

    public void setOldStateMachine(StateMachineWithStatusDTO oldStateMachine) {
        this.oldStateMachine = oldStateMachine;
    }

    public StateMachineWithStatusDTO getNewStateMachine() {
        return newStateMachine;
    }

    public void setNewStateMachine(StateMachineWithStatusDTO newStateMachine) {
        this.newStateMachine = newStateMachine;
    }

    public List<StateMachineSchemeStatusChangeItem> getStateMachineSchemeStatusChangeItems() {
        return stateMachineSchemeStatusChangeItems;
    }

    public void setStateMachineSchemeStatusChangeItems(List<StateMachineSchemeStatusChangeItem> stateMachineSchemeStatusChangeItems) {
        this.stateMachineSchemeStatusChangeItems = stateMachineSchemeStatusChangeItems;
    }
}
