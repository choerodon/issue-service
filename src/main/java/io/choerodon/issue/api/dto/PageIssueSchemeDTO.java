package io.choerodon.issue.api.dto;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class PageIssueSchemeDTO {

    private Long id;
    private String name;
    private String description;
    private Long organizationId;

    private Long objectVersionNumber;
    private List<PageIssueSchemeLineDTO> lineDTOS;

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

    public List<PageIssueSchemeLineDTO> getLineDTOS() {
        return lineDTOS;
    }

    public void setLineDTOS(List<PageIssueSchemeLineDTO> lineDTOS) {
        this.lineDTOS = lineDTOS;
    }
}
