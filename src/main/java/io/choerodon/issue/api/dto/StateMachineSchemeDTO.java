package io.choerodon.issue.api.dto;

import io.choerodon.issue.infra.feign.dto.ProjectDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineSchemeDTO {
    @ApiModelProperty(value = "状态机方案id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "状态机的状态（draft/active/create）")
    private String status;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "发布的状态（doing/done）")
    private String deployStatus;
    @ApiModelProperty(value = "关联的项目列表")
    private List<ProjectDTO> projectDTOs;
    @ApiModelProperty(value = "方案配置列表")
    private List<StateMachineSchemeConfigDTO> configDTOs;
    @ApiModelProperty(value = "方案配置列表（用于列表）")
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

    public String getDeployStatus() {
        return deployStatus;
    }

    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }
}
