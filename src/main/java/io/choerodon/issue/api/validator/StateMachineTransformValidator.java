package io.choerodon.issue.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.vo.StateMachineTransformVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Component
public class StateMachineTransformValidator {

    public void createValidate(StateMachineTransformVO transformVO) {
        if (StringUtils.isEmpty(transformVO.getStateMachineId())) {
            throw new CommonException("error.stateMachineNode.stateMachineId.empty");
        }
        if (StringUtils.isEmpty(transformVO.getName())) {
            throw new CommonException("error.stateMachineNode.name.empty");
        }
    }

    public void updateValidate(StateMachineTransformVO transformVO) {
        if (transformVO.getName() != null && transformVO.getName().length() == 0) {
            throw new CommonException("error.stateMachineNode.name.empty");
        }
    }
}
