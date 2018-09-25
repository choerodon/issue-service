package io.choerodon.issue.api.validator;

import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class StateMachineSchemeValidator {

    public void createValidate(StateMachineSchemeDTO schemeDTO){
        if (StringUtils.isEmpty(schemeDTO.getName())) {
            throw new CommonException("error.stateMachineScheme.name.empty");
        }
    }

    public void updateValidate(StateMachineSchemeDTO schemeDTO) {
        if (schemeDTO.getName() != null && schemeDTO.getName().length() == 0) {
            throw new CommonException("error.stateMachineScheme.name.empty");
        }
    }
}
