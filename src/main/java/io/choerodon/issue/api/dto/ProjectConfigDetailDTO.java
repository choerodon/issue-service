package io.choerodon.issue.api.dto;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */
public class ProjectConfigDetailDTO {
    private Long id;

    private Long projectId;

    private Long issueTypeSchemeId;

    private Long fieldConfigSchemeId;

    private Long pageIssueTypeSchemeId;

    private Long stateMachineSchemeId;

    private IssueTypeSchemeDTO issueTypeScheme;
    private StateMachineSchemeDTO stateMachineScheme;
    private PageIssueSchemeDTO pageIssueSchemeDTO;
    private FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getIssueTypeSchemeId() {
        return issueTypeSchemeId;
    }

    public void setIssueTypeSchemeId(Long issueTypeSchemeId) {
        this.issueTypeSchemeId = issueTypeSchemeId;
    }

    public Long getFieldConfigSchemeId() {
        return fieldConfigSchemeId;
    }

    public void setFieldConfigSchemeId(Long fieldConfigSchemeId) {
        this.fieldConfigSchemeId = fieldConfigSchemeId;
    }

    public Long getPageIssueTypeSchemeId() {
        return pageIssueTypeSchemeId;
    }

    public void setPageIssueTypeSchemeId(Long pageIssueTypeSchemeId) {
        this.pageIssueTypeSchemeId = pageIssueTypeSchemeId;
    }

    public Long getStateMachineSchemeId() {
        return stateMachineSchemeId;
    }

    public void setStateMachineSchemeId(Long stateMachineSchemeId) {
        this.stateMachineSchemeId = stateMachineSchemeId;
    }

    public IssueTypeSchemeDTO getIssueTypeScheme() {
        return issueTypeScheme;
    }

    public void setIssueTypeScheme(IssueTypeSchemeDTO issueTypeScheme) {
        this.issueTypeScheme = issueTypeScheme;
    }

    public StateMachineSchemeDTO getStateMachineScheme() {
        return stateMachineScheme;
    }

    public void setStateMachineScheme(StateMachineSchemeDTO stateMachineScheme) {
        this.stateMachineScheme = stateMachineScheme;
    }

    public PageIssueSchemeDTO getPageIssueSchemeDTO() {
        return pageIssueSchemeDTO;
    }

    public void setPageIssueSchemeDTO(PageIssueSchemeDTO pageIssueSchemeDTO) {
        this.pageIssueSchemeDTO = pageIssueSchemeDTO;
    }

    public FieldConfigSchemeDetailDTO getFieldConfigSchemeDetailDTO() {
        return fieldConfigSchemeDetailDTO;
    }

    public void setFieldConfigSchemeDetailDTO(FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO) {
        this.fieldConfigSchemeDetailDTO = fieldConfigSchemeDetailDTO;
    }
}
