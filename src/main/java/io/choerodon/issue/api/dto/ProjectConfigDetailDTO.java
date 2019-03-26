package io.choerodon.issue.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */
public class ProjectConfigDetailDTO {
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "关联的问题类型方案Map，key为applyType（应用类型）")
    private Map<String, IssueTypeSchemeDTO> issueTypeSchemeMap;
    @ApiModelProperty(value = "关联的状态机方案Map，key为applyType（应用类型）")
    private Map<String, StateMachineSchemeDTO> stateMachineSchemeMap;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Map<String, IssueTypeSchemeDTO> getIssueTypeSchemeMap() {
        return issueTypeSchemeMap;
    }

    public void setIssueTypeSchemeMap(Map<String, IssueTypeSchemeDTO> issueTypeSchemeMap) {
        this.issueTypeSchemeMap = issueTypeSchemeMap;
    }

    public Map<String, StateMachineSchemeDTO> getStateMachineSchemeMap() {
        return stateMachineSchemeMap;
    }

    public void setStateMachineSchemeMap(Map<String, StateMachineSchemeDTO> stateMachineSchemeMap) {
        this.stateMachineSchemeMap = stateMachineSchemeMap;
    }
}
