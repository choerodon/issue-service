package io.choerodon.issue.api.validator;

import io.choerodon.issue.api.vo.StateMachineSchemeVO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class StateMachineSchemeValidator {

    public void createValidate(StateMachineSchemeVO schemeVO){
        if (StringUtils.isEmpty(schemeVO.getName())) {
            throw new CommonException("error.stateMachineScheme.name.empty");
        }
    }

    public void updateValidate(StateMachineSchemeVO schemeVO) {
        if (schemeVO.getName() != null && schemeVO.getName().length() == 0) {
            throw new CommonException("error.stateMachineScheme.name.empty");
        }
    }
}
