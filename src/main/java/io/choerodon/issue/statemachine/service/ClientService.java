package io.choerodon.issue.statemachine.service;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.TransformInfo;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/10/11
 */
public interface ClientService {
    /**
     * 执行条件，验证，后置处理
     *
     * @param instanceId
     * @param targetStatusId
     * @param type
     * @param configDTOS
     * @return
     */
    ExecuteResult configExecute(Long instanceId, Long targetStatusId, String type,
                                String conditionStrategy, List<StateMachineConfigDTO> configDTOS);

    /**
     * 根据条件过滤转换
     *
     * @param instanceId
     * @param transformInfos
     * @return
     */
    List<TransformInfo> conditionFilter(Long instanceId, List<TransformInfo> transformInfos);
}
