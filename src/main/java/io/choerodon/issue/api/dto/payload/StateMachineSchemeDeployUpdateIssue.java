package io.choerodon.issue.api.dto.payload;

import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.infra.feign.dto.StatusDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/23
 */
public class StateMachineSchemeDeployUpdateIssue {
    private Long schemeId;
    private Long organizationId;
    private List<StateMachineSchemeChangeItem> changeItems;
    private List<ProjectConfig> projectConfigs;
    private List<RemoveStatusWithProject> removeStatusWithProjects;
    private List<StatusDTO> addStatuses;

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<ProjectConfig> getProjectConfigs() {
        return projectConfigs;
    }

    public void setProjectConfigs(List<ProjectConfig> projectConfigs) {
        this.projectConfigs = projectConfigs;
    }

    public List<StateMachineSchemeChangeItem> getChangeItems() {
        return changeItems;
    }

    public void setChangeItems(List<StateMachineSchemeChangeItem> changeItems) {
        this.changeItems = changeItems;
    }

    public List<StatusDTO> getAddStatuses() {
        return addStatuses;
    }

    public void setAddStatuses(List<StatusDTO> addStatuses) {
        this.addStatuses = addStatuses;
    }

    public List<RemoveStatusWithProject> getRemoveStatusWithProjects() {
        return removeStatusWithProjects;
    }

    public void setRemoveStatusWithProjects(List<RemoveStatusWithProject> removeStatusWithProjects) {
        this.removeStatusWithProjects = removeStatusWithProjects;
    }
}
