package io.choerodon.issue.statemachine.bean;

import java.lang.reflect.Method;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */
public class InvokeBean {
    private Method method;
    private Object object;
    private ConfigCodeDTO configCodeDTO;

    public InvokeBean(Method method, Object object, ConfigCodeDTO configCodeDTO) {
        this.method = method;
        this.object = object;
        this.configCodeDTO = configCodeDTO;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ConfigCodeDTO getConfigCodeDTO() {
        return configCodeDTO;
    }

    public void setConfigCodeDTO(ConfigCodeDTO configCodeDTO) {
        this.configCodeDTO = configCodeDTO;
    }
}
