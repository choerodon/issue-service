package io.choerodon.issue.infra.feign.dto;


/**
 * @author peng.jiang@hand-china.com
 */
public class ExecuteResult {
    Boolean isSuccess;
    Long resultStatusId;
    String errorMessage;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Long getResultStatusId() {
        return resultStatusId;
    }

    public void setResultStatusId(Long resultStatusId) {
        this.resultStatusId = resultStatusId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

