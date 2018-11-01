package io.choerodon.issue.api.dto;

import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */
public class ProjectConfigDetailDTO {

    private Long projectId;

    private Map<String, IssueTypeSchemeDTO> issueTypeSchemeMap;

    private Map<String, StateMachineSchemeDTO> stringStateMachineSchemeMap;

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

    public Map<String, StateMachineSchemeDTO> getStringStateMachineSchemeMap() {
        return stringStateMachineSchemeMap;
    }

    public void setStringStateMachineSchemeMap(Map<String, StateMachineSchemeDTO> stringStateMachineSchemeMap) {
        this.stringStateMachineSchemeMap = stringStateMachineSchemeMap;
    }
}
