package io.choerodon.issue.statemachine.spring;

import io.choerodon.issue.statemachine.StateMachineConfigMonitor;
import io.choerodon.issue.statemachine.annotation.*;
import io.choerodon.issue.statemachine.bean.ConfigCodeDTO;
import io.choerodon.issue.statemachine.bean.InvokeBean;
import io.choerodon.issue.statemachine.bean.PropertyData;
import io.choerodon.issue.statemachine.enums.StateMachineConfigType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */
@Component
public class ClientProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProcessor.class);

    @Autowired
    @Qualifier("stateMachineApplicationContextHelper")
    private ApplicationContextHelper applicationContextHelper;

    @Autowired
    @Qualifier("stateMachinePropertyData")
    private PropertyData stateMachinePropertyData;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            Condition condition = AnnotationUtils.getAnnotation(method, Condition.class);
            if (condition != null) {
                LOGGER.info("stateMachine client annotation condition:{}", condition);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(condition.code(), condition.name(), condition.description(), StateMachineConfigType.CONDITION);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = applicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(condition.code(), new InvokeBean(method, object, configCodeDTO));
            }
            Postpostition postpostition = AnnotationUtils.getAnnotation(method, Postpostition.class);
            if (postpostition != null) {
                LOGGER.info("stateMachine client annotation postpostition:{}", postpostition);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(postpostition.code(), postpostition.name(), postpostition.description(), StateMachineConfigType.POSTPOSITION);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = applicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(postpostition.code(), new InvokeBean(method, object, configCodeDTO));
            }
            Validator validator = AnnotationUtils.getAnnotation(method, Validator.class);
            if (validator != null) {
                LOGGER.info("stateMachine client annotation validator:{}", validator);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(validator.code(), validator.name(), validator.description(), StateMachineConfigType.VALIDATOR);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = applicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(validator.code(), new InvokeBean(method, object, configCodeDTO));
            }
            Trigger trigger = AnnotationUtils.getAnnotation(method, Trigger.class);
            if (trigger != null) {
                LOGGER.info("stateMachine client annotation trigger:{}", trigger);
                ConfigCodeDTO configCodeDTO = new ConfigCodeDTO(trigger.code(), trigger.name(), trigger.description(), StateMachineConfigType.TRIGGER);
                stateMachinePropertyData.getList().add(configCodeDTO);
                Object object = applicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.checkUniqueCode(configCodeDTO);
                StateMachineConfigMonitor.invokeBeanMap.put(trigger.code(), new InvokeBean(method, object, configCodeDTO));
            }
            //扫描UpdateStatus注解的方法
            UpdateStatus updateStatus = AnnotationUtils.getAnnotation(method, UpdateStatus.class);
            if (updateStatus != null) {
                LOGGER.info("stateMachine client annotation updateStatus:{}", updateStatus);
                StateMachineConfigMonitor.checkUniqueUpdateStatus();
                Object object = applicationContextHelper.getContext().getBean(method.getDeclaringClass());
                StateMachineConfigMonitor.setUpdateStatusBean(new InvokeBean(method, object, null));
            }
        }
        return bean;
    }
}
