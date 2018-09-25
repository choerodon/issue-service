package io.choerodon.issue.api.dto;

/**
 * @author peng.jiang@hand-china.com
 */
public class PageIssueSchemeLineDTO {

    private Long id;
    private Long schemeId;
    private Long issueTypeId;
    private Long pageSchemeId;

    private Long objectVersionNumber;
    private String issueTypeName;
    private String issueTypeIcon;
    private String pageSchemeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getPageSchemeId() {
        return pageSchemeId;
    }

    public void setPageSchemeId(Long pageSchemeId) {
        this.pageSchemeId = pageSchemeId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getIssueTypeName() {
        return issueTypeName;
    }

    public void setIssueTypeName(String issueTypeName) {
        this.issueTypeName = issueTypeName;
    }

    public String getIssueTypeIcon() {
        return issueTypeIcon;
    }

    public void setIssueTypeIcon(String issueTypeIcon) {
        this.issueTypeIcon = issueTypeIcon;
    }

    public String getPageSchemeName() {
        return pageSchemeName;
    }

    public void setPageSchemeName(String pageSchemeName) {
        this.pageSchemeName = pageSchemeName;
    }
}
