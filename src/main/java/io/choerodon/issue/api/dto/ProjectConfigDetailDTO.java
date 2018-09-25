package io.choerodon.issue.api.dto;

import io.choerodon.issue.infra.feign.dto.StateMachineDTO;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/9/10
 */
public class ProjectConfigDetailDTO {
    private Long id;

    private Long projectId;

    private Long issueTypeSchemeId;
    private String issueTypeSchemeName;

    private Long fieldConfigSchemeId;
    private String fieldConfigSchemeName;

    private Long pageIssueTypeSchemeId;
    private String pageIssueTypeSchemeName;

    private Long stateMachineSchemeId;
    private String stateMachineSchemeName;

    private List<IssueTypeDTO> issueTypeDTOList;
    private List<StateMachineDTO> stateMachineDTOList;
    private List<PageSchemeDetailDTO> pageSchemeDTOList;
    private List<FieldConfigDetailDTO> fieldConfigDTOList;

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


    public List<IssueTypeDTO> getIssueTypeDTOList() {
        return issueTypeDTOList;
    }

    public void setIssueTypeDTOList(List<IssueTypeDTO> issueTypeDTOList) {
        this.issueTypeDTOList = issueTypeDTOList;
    }

    public String getIssueTypeSchemeName() {
        return issueTypeSchemeName;
    }

    public void setIssueTypeSchemeName(String issueTypeSchemeName) {
        this.issueTypeSchemeName = issueTypeSchemeName;
    }

    public String getStateMachineSchemeName() {
        return stateMachineSchemeName;
    }

    public void setStateMachineSchemeName(String stateMachineSchemeName) {
        this.stateMachineSchemeName = stateMachineSchemeName;
    }

    public List<StateMachineDTO> getStateMachineDTOList() {
        return stateMachineDTOList;
    }

    public void setStateMachineDTOList(List<StateMachineDTO> stateMachineDTOList) {
        this.stateMachineDTOList = stateMachineDTOList;
    }

    public String getPageIssueTypeSchemeName() {
        return pageIssueTypeSchemeName;
    }

    public void setPageIssueTypeSchemeName(String pageIssueTypeSchemeName) {
        this.pageIssueTypeSchemeName = pageIssueTypeSchemeName;
    }



    public String getFieldConfigSchemeName() {
        return fieldConfigSchemeName;
    }

    public void setFieldConfigSchemeName(String fieldConfigSchemeName) {
        this.fieldConfigSchemeName = fieldConfigSchemeName;
    }


    public List<FieldConfigDetailDTO> getFieldConfigDTOList() {
        return fieldConfigDTOList;
    }

    public void setFieldConfigDTOList(List<FieldConfigDetailDTO> fieldConfigDTOList) {
        this.fieldConfigDTOList = fieldConfigDTOList;
    }

    public List<PageSchemeDetailDTO> getPageSchemeDTOList() {
        return pageSchemeDTOList;
    }

    public void setPageSchemeDTOList(List<PageSchemeDetailDTO> pageSchemeDTOList) {
        this.pageSchemeDTOList = pageSchemeDTOList;
    }
}
