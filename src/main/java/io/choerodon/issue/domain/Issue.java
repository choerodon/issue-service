package io.choerodon.issue.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author shinan.chen
 * @since 2018/8/2
 */

@ModifyAudit
@VersionAudit
@Table(name = "issue")
public class Issue extends AuditDomain {

    // 字段定义,修改记录时用到
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PRIORITY_ID = "priorityId";
    public static final String FIELD_STATUS_ID = "statusId";
    public static final String FIELD_HANDLER_ID = "handlerId";

    @Id
    @GeneratedValue
    private Long id;

    private String code;
    private Long issueTypeId;
    private String subject;
    private String description;
    private Long reporterId;
    private Long handlerId;
    private Long priorityId;
    private Long statusId;
    private String issueTag;
    private Date handleDate;
    private Date solveDate;
    private Long projectId;

    @Transient
    private String issueTypeName;
    @Transient
    private String issueTypeIcon;
    @Transient
    private String reporterName;
    @Transient
    private String reporterImageUrl;
    @Transient
    private String handlerName;
    @Transient
    private String handlerImageUrl;
    @Transient
    private String priorityName;
    @Transient
    private String statusName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getIssueTag() {
        return issueTag;
    }

    public void setIssueTag(String issueTag) {
        this.issueTag = issueTag;
    }

    public Date getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(Date handleDate) {
        this.handleDate = handleDate;
    }

    public Date getSolveDate() {
        return solveDate;
    }

    public void setSolveDate(Date solveDate) {
        this.solveDate = solveDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getIssueTypeName() {
        return issueTypeName;
    }

    public void setIssueTypeName(String issueTypeName) {
        this.issueTypeName = issueTypeName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterImageUrl() {
        return reporterImageUrl;
    }

    public void setReporterImageUrl(String reporterImageUrl) {
        this.reporterImageUrl = reporterImageUrl;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerImageUrl() {
        return handlerImageUrl;
    }

    public void setHandlerImageUrl(String handlerImageUrl) {
        this.handlerImageUrl = handlerImageUrl;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getIssueTypeIcon() {
        return issueTypeIcon;
    }

    public void setIssueTypeIcon(String issueTypeIcon) {
        this.issueTypeIcon = issueTypeIcon;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("code", code)
                .add("issueTypeId", issueTypeId)
                .add(FIELD_SUBJECT, subject)
                .add(FIELD_DESCRIPTION, description)
                .add("reporterId", reporterId)
                .add(FIELD_HANDLER_ID, handlerId)
                .add(FIELD_PRIORITY_ID, priorityId)
                .add(FIELD_STATUS_ID, statusId)
                .add("issueTag", issueTag)
                .add("handleDate", handleDate)
                .add("solveDate", solveDate)
                .add("projectId", projectId)
                .add("issueTypeName", issueTypeName)
                .add("issueTypeIcon", issueTypeIcon)
                .add("reporterName", reporterName)
                .add("reporterImageUrl", reporterImageUrl)
                .add("handlerName", handlerName)
                .add("handlerImageUrl", handlerImageUrl)
                .add("priorityName", priorityName)
                .add("statusName", statusName)
                .toString();
    }
}
