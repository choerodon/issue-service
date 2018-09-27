package io.choerodon.issue.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
@ModifyAudit
@VersionAudit
@Table(name = "issue_type")
public class IssueType extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private String icon;
    private String name;
    private String description;
    private Long organizationId;
    private String colour;

    @Transient
    private BigDecimal sequence;

    @Transient
    private StateMachineSchemeConfig stateMachineSchemeConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public StateMachineSchemeConfig getStateMachineSchemeConfig() {
        return stateMachineSchemeConfig;
    }

    public void setStateMachineSchemeConfig(StateMachineSchemeConfig stateMachineSchemeConfig) {
        this.stateMachineSchemeConfig = stateMachineSchemeConfig;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
