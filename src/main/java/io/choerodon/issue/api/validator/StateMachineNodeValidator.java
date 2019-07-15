package io.choerodon.issue.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.vo.StateMachineNodeVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Component
public class StateMachineNodeValidator {

    public void createValidate(StateMachineNodeVO nodeVO) {
        if (StringUtils.isEmpty(nodeVO.getStateMachineId())) {
            throw new CommonException("error.stateMachineNode.stateMachineId.empty");
        }
        if (StringUtils.isEmpty(nodeVO.getStatusId()) && nodeVO.getStatusVO() == null) {
            throw new CommonException("error.stateMachineNode.state.null");
        }
        if (StringUtils.isEmpty(nodeVO.getStatusId()) && nodeVO.getStatusVO() != null && StringUtils.isEmpty(nodeVO.getStatusVO().getName())) {
            throw new CommonException("error.stateMachineNode.state.name.empty");
        }
    }

    public void updateValidate(StateMachineNodeVO nodeVO) {
        if (StringUtils.isEmpty(nodeVO.getStatusId()) && nodeVO.getStatusVO() == null) {
            throw new CommonException("error.stateMachineNode.state.null");
        }
        if (nodeVO.getStatusVO() != null && StringUtils.isEmpty(nodeVO.getStatusVO().getName())) {
            throw new CommonException("error.stateMachineNode.state.name.empty");
        }
    }
}
