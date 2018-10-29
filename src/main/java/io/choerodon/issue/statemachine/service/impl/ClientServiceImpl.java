package io.choerodon.issue.statemachine.service.impl;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.issue.statemachine.StateMachineConfigMonitor;
import io.choerodon.issue.statemachine.bean.InvokeBean;
import io.choerodon.issue.statemachine.bean.TransformInfo;
import io.choerodon.issue.statemachine.enums.StateMachineConfigType;
import io.choerodon.issue.statemachine.enums.TransformConditionStrategy;
import io.choerodon.issue.statemachine.service.ClientService;
import io.choerodon.issue.statemachine.spring.ClientProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/10/11
 */
@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientProcessor.class);

    @Override
    public List<TransformInfo> conditionFilter(Long instanceId, List<TransformInfo> transformDTOS) {
        logger.info("stateMachine client conditionFilter start: instanceId:{}, transformInfos:{}", instanceId, transformDTOS);
        if (transformDTOS == null || transformDTOS.isEmpty()) {
            return Collections.emptyList();
        }
        List<TransformInfo> resultTransforms = new ArrayList<>();
        transformDTOS.forEach(transformInfo -> {
            List<StateMachineConfigDTO> configDTOS = transformInfo.getConditions();
            ExecuteResult result = configExecuteCondition(instanceId, transformInfo.getEndStatusId(), transformInfo.getConditionStrategy(), configDTOS);
            if (result.getSuccess()) {
                logger.info("stateMachine client conditionFilter transform match condition: instanceId:{}, transformId:{}", instanceId, transformInfo.getId());
                resultTransforms.add(transformInfo);
            } else {
                logger.info("stateMachine client conditionFilter transform not match condition: instanceId:{}, transformId:{}", instanceId, transformInfo.getId());
            }
        });
        return resultTransforms;
    }

    /**
     * 执行条件
     *
     * @param instanceId
     * @param conditionStrategy
     * @param configDTOS
     * @return
     */
    @Override
    public ExecuteResult configExecuteCondition(Long instanceId, Long targetStatusId, String conditionStrategy, List<StateMachineConfigDTO> configDTOS) {
        logger.info("stateMachine client configExecuteCondition start: instanceId:{}, configDTOS:{}", instanceId, configDTOS);
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = true;
        //执行代码中配置的条件
        for (StateMachineConfigDTO configDTO : configDTOS) {
            isSuccess = methodInvokeBean(StateMachineConfigType.CONDITION, configDTO, instanceId);

            //根据不同的条件策略返回不同结果
            if (conditionStrategy.equals(TransformConditionStrategy.ALL)) {
                if (!isSuccess) {
                    executeResult.setErrorMessage("配置类型【条件】:" + configDTO.getCode() + "执行不通过");
                    break;
                } else {
                    executeResult.setErrorMessage("配置类型【条件】:条件全部符合");
                }
            } else {
                if (isSuccess) {
                    executeResult.setErrorMessage("配置类型【条件】:" + configDTO.getCode() + "执行通过");
                    break;
                } else {
                    executeResult.setErrorMessage("配置类型【条件】:没有符合的条件类型");
                }
            }
        }
        executeResult.setSuccess(isSuccess);
        return executeResult;
    }

    /**
     * 执行验证
     *
     * @param instanceId
     * @param targetStatusId
     * @param configDTOS
     * @return
     */
    @Override
    public ExecuteResult configExecuteValidator(Long instanceId, Long targetStatusId, List<StateMachineConfigDTO> configDTOS) {
        logger.info("stateMachine client configExecuteValidator start: instanceId:{}, configDTOS:{}", instanceId, configDTOS);
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = true;
        //执行代码中配置的验证
        for (StateMachineConfigDTO configDTO : configDTOS) {
            isSuccess = methodInvokeBean(StateMachineConfigType.VALIDATOR, configDTO, instanceId);
            if (!isSuccess) {
                executeResult.setErrorMessage("配置类型【验证】:" + configDTO.getCode() + "执行不通过");
                break;
            }
        }
        executeResult.setSuccess(isSuccess);
        return executeResult;
    }

    /**
     * 执行后置动作
     *
     * @param instanceId
     * @param targetStatusId
     * @param configDTOS
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecuteResult configExecutePostposition(Long instanceId, Long targetStatusId, List<StateMachineConfigDTO> configDTOS) {
        logger.info("stateMachine client configExecutePostposition start: instanceId:{}, configDTOS:{}", instanceId, configDTOS);
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = true;
        //执行后置动作要先调用状态更新
        InvokeBean updateInvokeBean = StateMachineConfigMonitor.updateStatusBean;
        if (updateInvokeBean != null) {
            Object object = updateInvokeBean.getObject();
            Method method = updateInvokeBean.getMethod();
            try {
                method.invoke(object, instanceId, targetStatusId);
                logger.info("stateMachine client configExecute updateStatus with method {}: instanceId:{}, targetStatusId:{}", method.getName(), instanceId, targetStatusId);
            } catch (Exception e) {
                logger.error("stateMachine client configExecute updateStatus invoke error {}", e.getMessage());
                e.printStackTrace();
                isSuccess = false;
            }
        } else {
            logger.error("stateMachine client configExecute updateStatus invokeBean not found");
            isSuccess = false;
        }
        //执行代码中配置的后置动作
        if (isSuccess) {
            for (StateMachineConfigDTO configDTO : configDTOS) {
                isSuccess = methodInvokeBean(StateMachineConfigType.POSTPOSITION, configDTO, instanceId);
                if (!isSuccess) {
                    executeResult.setErrorMessage("配置类型【后置动作】：" + configDTO.getCode() + "执行不通过");
                    break;
                }
            }
        }else{
            executeResult.setErrorMessage("状态更新失败");
        }
        executeResult.setSuccess(isSuccess);
        executeResult.setResultStatusId(targetStatusId);
        return executeResult;
    }

    public Boolean methodInvokeBean(String type, StateMachineConfigDTO configDTO, Long instanceId) {
        Boolean isSuccess = true;
        InvokeBean invokeBean = StateMachineConfigMonitor.invokeBeanMap.get(configDTO.getCode());
        if (invokeBean != null) {
            Object object = invokeBean.getObject();
            Method method = invokeBean.getMethod();
            try {
                if(type.equals(StateMachineConfigType.POSTPOSITION)){
                    method.invoke(object, instanceId, configDTO);
                }else{
                    isSuccess = (Boolean) method.invoke(object, instanceId, configDTO);
                }
                logger.info("stateMachine client {} {} with method {}: instanceId:{}, result:{}", type, configDTO.getCode(), method.getName(), instanceId, isSuccess);
            } catch (Exception e) {
                logger.error("stateMachine client {} {} with method {} invoke error {}", type, configDTO.getCode(), method.getName(), e.getMessage());
                e.printStackTrace();
                isSuccess = false;
            }
        } else {
            logger.error("stateMachine client {} {} invokeBean not found", type, configDTO.getCode());
            isSuccess = false;
        }
        return isSuccess;
    }
}
