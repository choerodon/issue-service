package io.choerodon.issue.statemachine;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.statemachine.bean.ConfigCodeDTO;
import io.choerodon.issue.statemachine.bean.InvokeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */
public class StateMachineConfigMonitor {
    public static final Logger logger = LoggerFactory.getLogger(StateMachineConfigMonitor.class);
    public static final Map<String, InvokeBean> invokeBeanMap = new HashMap<>();
    public static void checkUniqueCode(ConfigCodeDTO codeDTO){
        Set<Map.Entry<String, InvokeBean>> invokes = invokeBeanMap.entrySet();
        invokes.forEach(x->{
            ConfigCodeDTO configCodeDTO = x.getValue().getConfigCodeDTO();
            if(configCodeDTO.getCode().equals(codeDTO.getCode())&&configCodeDTO.getType().equals(codeDTO.getType())){
                logger.error("StateMachineConfigMonitor annotation code duplication: {}",codeDTO);
                throw new CommonException("error.checkUniqueCode.duplication");
            }
        });
    }
}
