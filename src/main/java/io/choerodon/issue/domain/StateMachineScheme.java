package io.choerodon.issue.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@ModifyAudit
@VersionAudit
@Table(name = "state_machine_scheme")
public class StateMachineScheme extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private Long defaultStateMachineId;
    private Long organizationId;

    @Transient
    private List<StateMachineSchemeConfig> schemeConfigs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDefaultStateMachineId() {
        return defaultStateMachineId;
    }

    public void setDefaultStateMachineId(Long defaultStateMachineId) {
        this.defaultStateMachineId = defaultStateMachineId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<StateMachineSchemeConfig> getSchemeConfigs() {
        return schemeConfigs;
    }

    public void setSchemeConfigs(List<StateMachineSchemeConfig> schemeConfigs) {
        this.schemeConfigs = schemeConfigs;
    }
}
