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
    public static InvokeBean updateStatusBean = null;

    public static void setUpdateStatusBean(InvokeBean updateStatusBean) {
        StateMachineConfigMonitor.updateStatusBean = updateStatusBean;
    }

    /**
     * 校验code不能重复
     *
     * @param codeDTO
     */
    public static void checkUniqueCode(ConfigCodeDTO codeDTO) {
        Set<Map.Entry<String, InvokeBean>> invokes = invokeBeanMap.entrySet();
        invokes.forEach(x -> {
            ConfigCodeDTO configCodeDTO = x.getValue().getConfigCodeDTO();
            if (configCodeDTO.getCode().equals(codeDTO.getCode())) {
                logger.error("StateMachineConfigMonitor annotation code duplication: {}", codeDTO);
                throw new CommonException("error.checkUniqueCode.duplication");
            }
        });
    }

    /**
     * 校验updateStatus注解不能注解多个方法
     */
    public static void checkUniqueUpdateStatus() {
        if (updateStatusBean != null) {
            logger.error("StateMachineConfigMonitor annotation updateStatus duplication");
            throw new CommonException("error.checkUniqueUpdateStatus.duplication");
        }
    }
}
