package io.choerodon.issue.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/29.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueTypeSchemeRelationDTO {

    @ApiModelProperty(value = "问题类型方案id")
    private Long issueTypeSchemeId;

    @ApiModelProperty(value = "方案名称")
    private String issueTypeSchemeName;

    public Long getIssueTypeSchemeId() {
        return issueTypeSchemeId;
    }

    public void setIssueTypeSchemeId(Long issueTypeSchemeId) {
        this.issueTypeSchemeId = issueTypeSchemeId;
    }

    public String getIssueTypeSchemeName() {
        return issueTypeSchemeName;
    }

    public void setIssueTypeSchemeName(String issueTypeSchemeName) {
        this.issueTypeSchemeName = issueTypeSchemeName;
    }
}
