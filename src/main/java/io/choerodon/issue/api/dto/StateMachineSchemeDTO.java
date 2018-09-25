package io.choerodon.issue.api.dto;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineSchemeDTO {

    private Long id;
    private String name;
    private String description;
    private Long defaultStateMachineId;
    private Long organizationId;
    private Long objectVersionNumber;

    private List<StateMachineSchemeConfigDTO> configDTOs;

    private List<StateMachineSchemeConfigViewDTO> viewDTOs;

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
}
