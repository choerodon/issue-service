package io.choerodon.issue.api.validator;

import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Component
public class IssueValidator {

    public void createIssue(String type){
        if(type!=null&&type.equals("test")){
            throw new CommonException("error.type.illegal");
        }
    }
}
