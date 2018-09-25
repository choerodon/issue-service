package io.choerodon.issue.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author peng.jiang@hand-china.com
 */
@ModifyAudit
@VersionAudit
@Table(name = "page_issue_scheme_line")
public class PageIssueSchemeLine extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private Long schemeId;
    private Long issueTypeId;
    private Long pageSchemeId;

    @Transient
    private String issueTypeName;
    @Transient
    private String issueTypeIcon;
    @Transient
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
