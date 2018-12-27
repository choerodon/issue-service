package io.choerodon.issue.api.dto;

import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */
public class ProjectConfigDetailDTO {

    private Long projectId;

    private Map<String, IssueTypeSchemeDTO> issueTypeSchemeMap;

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
