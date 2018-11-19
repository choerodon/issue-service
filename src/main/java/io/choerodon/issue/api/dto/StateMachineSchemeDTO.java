package io.choerodon.issue.api.dto;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineSchemeDTO {

    private Long id;
    private String name;
    private String description;
    private Long organizationId;
    private String status;
    private Long objectVersionNumber;

    private List<ProjectDTO> projectDTOs;

    private List<StateMachineSchemeConfigDTO> configDTOs;

    private List<StateMachineSchemeConfigViewDTO> viewDTOs;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<StateMachineSchemeConfigViewDTO> getViewDTOs() {
        return viewDTOs;
    }

    public void setViewDTOs(List<StateMachineSchemeConfigViewDTO> viewDTOs) {
        this.viewDTOs = viewDTOs;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<StateMachineSchemeConfigDTO> getConfigDTOs() {
        return configDTOs;
    }

    public void setConfigDTOs(List<StateMachineSchemeConfigDTO> configDTOs) {
        this.configDTOs = configDTOs;
    }

    public List<ProjectDTO> getProjectDTOs() {
        return projectDTOs;
    }

    public void setProjectDTOs(List<ProjectDTO> projectDTOs) {
        this.projectDTOs = projectDTOs;
    }
}
