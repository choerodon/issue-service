package io.choerodon.issue.api.validator;

import io.choerodon.issue.api.dto.PageIssueTypeSchemeDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class PageIssueSchemeValidator {

    public void createValidate(PageIssueTypeSchemeDTO schemeDTO){
        if (StringUtils.isEmpty(schemeDTO.getName())) {
            throw new CommonException("error.pageIssueScheme.name.empty");
        }
    }

    public void updateValidate(PageIssueTypeSchemeDTO schemeDTO) {
        if (schemeDTO.getName() != null && schemeDTO.getName().length() == 0) {
            throw new CommonException("error.pageIssueScheme.name.empty");
        }
    }
}
