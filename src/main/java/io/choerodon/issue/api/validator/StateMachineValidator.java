package io.choerodon.issue.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.vo.StateMachineVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Component
public class StateMachineValidator {

    public void createValidate(StateMachineVO stateMachineVO) {
        if (StringUtils.isEmpty(stateMachineVO.getName())) {
            throw new CommonException("error.stateMachine.name.empty");
        }
    }

    public void updateValidate(StateMachineVO stateMachineVO) {
        if (stateMachineVO.getName() != null && stateMachineVO.getName().length() == 0) {
            throw new CommonException("error.stateMachine.name.empty");
        }
    }
}
