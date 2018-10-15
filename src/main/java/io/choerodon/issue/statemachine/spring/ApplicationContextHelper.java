package io.choerodon.issue.statemachine.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */
@Component("stateMachineApplicationContextHelper")
public class ApplicationContextHelper implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getContext(){
        return this.applicationContext;
    }
}
