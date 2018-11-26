package io.choerodon.issue.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

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
    private String typeCode;
    private Boolean initialize;
    private Long objectVersionNumber;

    //用于做状态机方案的配置时
    private String stateMachineName;
    private Long stateMachineId;

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

    public String getStateMachineName() {
        return stateMachineName;
    }

    public void setStateMachineName(String stateMachineName) {
        this.stateMachineName = stateMachineName;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Boolean getInitialize() {
        return initialize;
    }

    public void setInitialize(Boolean initialize) {
        this.initialize = initialize;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("icon", icon)
                .add("description", description)
                .add("organizationId", organizationId)
                .add("colour", colour)
                .add("typeCode", typeCode)
                .add("initialize", initialize)
                .add("objectVersionNumber", objectVersionNumber)
                .add("stateMachineName", stateMachineName)
                .add("stateMachineId", stateMachineId)
                .toString();
    }
}
