package io.choerodon.issue.statemachine.service.impl;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import io.choerodon.issue.statemachine.StateMachineConfigMonitor;
import io.choerodon.issue.statemachine.bean.InvokeBean;
import io.choerodon.issue.statemachine.enums.StateMachineConfigType;
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
@Transactional(rollbackFor = Exception.class)
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientProcessor.class);

    @Override
    public ExecuteResult configExecute(Long instanceId, Long targetStatusId, String type, String conditionStrategy, List<StateMachineConfigDTO> configDTOS) {
        logger.info("stateMachine client configExecute start: type:{}, instanceId:{}, configDTOS:{}", type, instanceId, configDTOS);
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = true;
        //若是执行后置动作，要先调用状态更新
        if (type.equals(StateMachineConfigType.POSTPOSITION)) {
            InvokeBean invokeBean = StateMachineConfigMonitor.updateStatusBean;
            if (invokeBean != null) {
                Object object = invokeBean.getObject();
                Method method = invokeBean.getMethod();
                try {
                    method.invoke(object, instanceId, targetStatusId);
                    logger.info("stateMachine client configExecute updateStatus with method {}: instanceId:{}, targetStatusId:{}", method.getName(), instanceId, targetStatusId);
                } catch (Exception e) {
                    logger.error("stateMachine client configExecute updateStatus invoke error {}", e.getMessage());
                    isSuccess = false;
                }
            }else{
                logger.error("stateMachine client configExecute updateStatus invokeBean not found");
                isSuccess = false;
            }
        }
        //执行代码中配置的条件、校验、后置动作
        if (isSuccess) {
            for (StateMachineConfigDTO configDTO : configDTOS) {
                InvokeBean invokeBean = StateMachineConfigMonitor.invokeBeanMap.get(configDTO.getCode());
                if (invokeBean != null) {
                    Object object = invokeBean.getObject();
                    Method method = invokeBean.getMethod();
                    try {
                        isSuccess = (Boolean) method.invoke(object, instanceId, configDTO);
                        logger.info("stateMachine client configExecute {} with method {}: instanceId:{}", configDTO.getCode(), method.getName(), instanceId);
                    } catch (Exception e) {
                        logger.error("stateMachine client configExecute {} with method {} invoke error {}", configDTO.getCode(), method.getName(), e.getMessage());
                        isSuccess = false;
                    }
                } else {
                    logger.error("stateMachine client configExecute {} invokeBean not found", configDTO.getCode());
                    isSuccess = false;
                }
                if (!isSuccess) {
                    break;
                }
            }
        }
        executeResult.setSuccess(isSuccess);
        return executeResult;
    }

    @Override
    public List<TransformInfo> conditionFilter(Long instanceId, List<TransformInfo> transformInfos) {
        logger.info("stateMachine client conditionFilter start: instanceId:{}, transformInfos:{}", instanceId, transformInfos);
        if (transformInfos == null || transformInfos.isEmpty()) {
            return Collections.emptyList();
        }
        List<TransformInfo> resultTransforms = new ArrayList<>();
        transformInfos.forEach(transformInfo -> {
            List<StateMachineConfigDTO> configDTOS = transformInfo.getConditions();
            ExecuteResult executeResult = configExecute(instanceId, transformInfo.getEndStatusId(), StateMachineConfigType.CONDITION, transformInfo.getConditionStrategy(), configDTOS);
            if (executeResult.getSuccess()) {
                logger.info("stateMachine client conditionFilter transform match condition: instanceId:{}, transformId:{}", instanceId, transformInfo.getId());
                resultTransforms.add(transformInfo);
            }else{
                logger.info("stateMachine client conditionFilter transform not match condition: instanceId:{}, transformId:{}", instanceId, transformInfo.getId());
            }
        });
        return resultTransforms;
    }
}
