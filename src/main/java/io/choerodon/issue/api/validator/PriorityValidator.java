package io.choerodon.issue.api.validator;

import io.choerodon.issue.api.dto.PriorityDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */
@Component
public class PriorityValidator {


    public void createValidate(PriorityDTO priorityDTO) {
        if (StringUtils.isEmpty(priorityDTO.getName())) {
            throw new CommonException("error.priority.create.name.empty");
        }
        if (StringUtils.isEmpty(priorityDTO.getColour())) {
            throw new CommonException("error.priority.create.colour.empty");
        }

    }

    public void updateValidate(PriorityDTO priorityDTO) {
        if (priorityDTO.getName() != null && priorityDTO.getName().length() == 0) {
            throw new CommonException("error.priority.update.name.empty");
        }

    }


}
