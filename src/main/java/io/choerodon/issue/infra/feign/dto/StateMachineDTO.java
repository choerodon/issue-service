package io.choerodon.issue.infra.feign.dto;

import io.choerodon.issue.api.dto.StateMachineSchemeDTO;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineDTO {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long organizationId;

    List<StateMachineSchemeDTO> stateMachineSchemeDTOs;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<StateMachineSchemeDTO> getStateMachineSchemeDTOs() {
        return stateMachineSchemeDTOs;
    }

    public void setStateMachineSchemeDTOs(List<StateMachineSchemeDTO> stateMachineSchemeDTOs) {
        this.stateMachineSchemeDTOs = stateMachineSchemeDTOs;
    }
}
