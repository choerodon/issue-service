package io.choerodon.issue.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author jiameng.cao
 * @date 2018/8/27
 */

@ModifyAudit
@VersionAudit
@Table(name = "field_config_scheme_line")
public class FieldConfigSchemeLine extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private Long schemeId;
    private Long issueTypeId;
    private Long fieldConfigId;

    @Transient
    private String issueTypeName;
    @Transient
    private String issueTypeIcon;

    @Transient
    private String fieldConfigName;

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

    public Long getFieldConfigId() {
        return fieldConfigId;
    }

    public void setFieldConfigId(Long fieldConfigId) {
        this.fieldConfigId = fieldConfigId;
    }

    public String getIssueTypeName() {
        return issueTypeName;
    }

    public void setIssueTypeName(String issueTypeName) {
        this.issueTypeName = issueTypeName;
    }

    public String getFieldConfigName() {
        return fieldConfigName;
    }

    public void setFieldConfigName(String fieldConfigName) {
        this.fieldConfigName = fieldConfigName;
    }

    public String getIssueTypeIcon() {
        return issueTypeIcon;
    }

    public void setIssueTypeIcon(String issueTypeIcon) {
        this.issueTypeIcon = issueTypeIcon;
    }
}
