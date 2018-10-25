package io.choerodon.issue.api.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
public class IssueTypeSchemeDTO {
    private Long id;

    @NotNull(message = "error.name.null")
    private String name;
    private String description;

    @NotNull(message = "error.defaultIssueTypeId.null")
    /**
     * 若无默认问题类型，传0L
     */
    private Long defaultIssueTypeId;
    private Long organizationId;
    private String type;
    private Long objectVersionNumber;

    private List<IssueTypeDTO> issueTypes;

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

    public Long getDefaultIssueTypeId() {
        return defaultIssueTypeId;
    }

    public void setDefaultIssueTypeId(Long defaultIssueTypeId) {
        this.defaultIssueTypeId = defaultIssueTypeId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<IssueTypeDTO> getIssueTypes() {
        return issueTypes;
    }

    public void setIssueTypes(List<IssueTypeDTO> issueTypes) {
        this.issueTypes = issueTypes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
