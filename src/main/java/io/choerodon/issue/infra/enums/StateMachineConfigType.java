package io.choerodon.issue.infra.enums;

/**
 * @author peng.jiang@hand-china.com
 */
public enum StateMachineConfigType {

    TYPE_CONDITION("condition"),  //条件
    TYPE_VALIDATOR("validator"),  //验证器
    TYPE_TRIGGER("trigger"),  //触发器
    TYPE_POSTPOSITION("postposition"); //后置功能

    private String value;

    StateMachineConfigType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
