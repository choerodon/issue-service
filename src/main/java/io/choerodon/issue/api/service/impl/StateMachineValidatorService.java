package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineConfigService;
import io.choerodon.issue.infra.enums.StateMachineConfigEnums;
import io.choerodon.issue.infra.enums.StateMachineConfigType;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Service
public class StateMachineValidatorService implements StateMachineConfigService {

    @Autowired
    private IssueService issueService;

    @Override
    public String configType() {
        return StateMachineConfigType.TYPE_VALIDATOR.value();
    }

    /**
     * 条件执行 入口
     *
     * @param instanceId
     * @param conditionStrategy
     * @param configDTOS
     * @return
     */
    @Override
    public Boolean configExecute(Long instanceId, String conditionStrategy, List<StateMachineConfigDTO> configDTOS) {
        for (StateMachineConfigDTO configDTO : configDTOS) {
            switch (configDTO.getCode()) {
                case StateMachineConfigEnums.VALIDATOR:
                    break;
                default:
                    throw new CommonException("error.configDTO.validator.noMatch");
            }
        }
        return true;
    }

}
