package io.choerodon.issue.infra.feign.dto;


/**
 * @author peng.jiang@hand-china.com
 */
public class ExecuteResult {
    Boolean isSuccess;
    Long resultStateId;
    String errorMessage;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Long getResultStateId() {
        return resultStateId;
    }

    public void setResultStateId(Long resultStateId) {
        this.resultStateId = resultStateId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

