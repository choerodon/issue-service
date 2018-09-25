package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineTransfDTO;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public interface StateMachineConfigService {

    /**
     * 当前实现类处理的类型.
     *
     * @return
     */
    String configType();

    /**
     * 判断配置类型（condition,validator,trigger,postposition）是否匹配.
     *
     * @param configType 配置类型
     * @return
     */
    default boolean matchConfigType(String configType) {
        return configType().equalsIgnoreCase(configType);
    }

    /**
     * 条件 过滤
     *
     * @param instanceId
     * @param configDTOS
     * @return
     */
    public default List<StateMachineTransfDTO> conditionFilter(Long instanceId, List<StateMachineTransfDTO> transfDTOS) {
        return transfDTOS;
    }

    /**
     * 条件，验证 执行
     *
     * @param instanceId
     * @param conditionStrategy
     * @param configDTOS
     * @return
     */
    public default Boolean configExecute(Long instanceId, String conditionStrategy, List<StateMachineConfigDTO> configDTOS){
        return true;
    }

    /**
     * 后置处理 执行
     *
     * @param instanceId
     * @param targetStateId
     * @param configDTOS
     * @return
     */
    public default Boolean configExecute(Long instanceId, Long targetStateId, List<StateMachineConfigDTO> configDTOS){
        return true;
    }
}
