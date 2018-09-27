package io.choerodon.issue.api.dto;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
public class IssueTypeDTO {
    private Long id;

    @NotNull(message = "error.name.null")
    private String name;
    private String icon;
    private String description;
    private Long organizationId;
    private String colour;

    private Long objectVersionNumber;

    private StateMachineSchemeConfigDTO stateMachineSchemeConfigDTO;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public StateMachineSchemeConfigDTO getStateMachineSchemeConfigDTO() {
        return stateMachineSchemeConfigDTO;
    }

    public void setStateMachineSchemeConfigDTO(StateMachineSchemeConfigDTO stateMachineSchemeConfigDTO) {
        this.stateMachineSchemeConfigDTO = stateMachineSchemeConfigDTO;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
