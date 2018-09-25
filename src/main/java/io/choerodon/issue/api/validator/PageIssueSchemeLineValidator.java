package io.choerodon.issue.api.validator;

import io.choerodon.issue.domain.PageIssueSchemeLine;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class PageIssueSchemeLineValidator {

    public void validate(PageIssueSchemeLine line){
        if (line.getIssueTypeId() == null) {
            throw new CommonException("error.pageIssueSchemeLine.issueTypeId.null");
        }
        if (line.getPageSchemeId() == null) {
            throw new CommonException("error.pageIssueSchemeLine.pageSchemeId.null");
        }
    }
}
