package io.choerodon.issue.api.vo.payload;

import io.choerodon.issue.api.vo.StatusVO;

/**
 * @author shinan.chen
 * @date 2018/11/23
 */
public class StateMachineSchemeStatusChangeItem {
    private StatusVO oldStatus;
    private StatusVO newStatus;

    public StateMachineSchemeStatusChangeItem() {
    }

    public StateMachineSchemeStatusChangeItem(StatusVO oldStatus, StatusVO newStatus) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public StatusVO getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(StatusVO oldStatus) {
        this.oldStatus = oldStatus;
    }

    public StatusVO getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(StatusVO newStatus) {
        this.newStatus = newStatus;
    }
}
