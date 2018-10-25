package io.choerodon.issue.statemachine.service;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.issue.statemachine.bean.TransformInfo;

import java.util.List;

/**
 * 状态机客户端回调service
 *
 * @author shinan.chen
 * @date 2018/10/11
 */
public interface ClientService {

    /**
     * 根据条件过滤转换
     *
     * @param instanceId
     * @param transformDTOS
     * @return
     */
    List<TransformInfo> conditionFilter(Long instanceId, List<TransformInfo> transformDTOS);


    /**
     * 执行条件
     *
     * @param instanceId
     * @param targetStatusId
     * @param conditionStrategy
     * @param configDTOS
     * @return
     */
    ExecuteResult configExecuteCondition(Long instanceId, Long targetStatusId, String conditionStrategy, List<StateMachineConfigDTO> configDTOS);

    /**
     * 执行验证
     *
     * @param instanceId
     * @param targetStatusId
     * @param configDTOS
     * @return
     */
    ExecuteResult configExecuteValidator(Long instanceId, Long targetStatusId, List<StateMachineConfigDTO> configDTOS);

    /**
     * 执行后置动作，单独出来，才能生效回归
     *
     * @param instanceId
     * @param targetStatusId
     * @param configDTOS
     * @return
     */
    ExecuteResult configExecutePostposition(Long instanceId, Long targetStatusId, List<StateMachineConfigDTO> configDTOS);
}
