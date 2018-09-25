package io.choerodon.issue.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author shinan.chen
 * @date 2018/9/4
 */
@ModifyAudit
@VersionAudit
@Table(name = "project_config")
public class ProjectConfig extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private Long projectId;
    private Long issueTypeSchemeId;
    private Long fieldConfigSchemeId;
    private Long pageIssueTypeSchemeId;
    private Long stateMachineSchemeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getIssueTypeSchemeId() {
        return issueTypeSchemeId;
    }

    public void setIssueTypeSchemeId(Long issueTypeSchemeId) {
        this.issueTypeSchemeId = issueTypeSchemeId;
    }

    public Long getFieldConfigSchemeId() {
        return fieldConfigSchemeId;
    }

    public void setFieldConfigSchemeId(Long fieldConfigSchemeId) {
        this.fieldConfigSchemeId = fieldConfigSchemeId;
    }

    public Long getPageIssueTypeSchemeId() {
        return pageIssueTypeSchemeId;
    }

    public void setPageIssueTypeSchemeId(Long pageIssueTypeSchemeId) {
        this.pageIssueTypeSchemeId = pageIssueTypeSchemeId;
    }

    public Long getStateMachineSchemeId() {
        return stateMachineSchemeId;
    }

    public void setStateMachineSchemeId(Long stateMachineSchemeId) {
        this.stateMachineSchemeId = stateMachineSchemeId;
    }
}
