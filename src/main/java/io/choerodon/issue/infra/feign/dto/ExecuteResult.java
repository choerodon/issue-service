package io.choerodon.issue.infra.feign.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * @author peng.jiang@hand-china.com
 */
@Setter
@Getter
public class ExecuteResult {
    Boolean isSuccess;
    Long resultStateId;
    String errorMessage;
}

