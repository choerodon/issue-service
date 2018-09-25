package io.choerodon.issue.api.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/23
 */
public class PageSchemeDetailDTO {
    private Long id;

    @NotNull(message = "error.name.null")
    private String name;
    private String description;
    private Long organizationId;

    private Long objectVersionNumber;

    private List<PageSchemeLineDTO> pageSchemeLineDTOS;

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

    public List<PageSchemeLineDTO> getPageSchemeLineDTOS() {
        return pageSchemeLineDTOS;
    }

    public void setPageSchemeLineDTOS(List<PageSchemeLineDTO> pageSchemeLineDTOS) {
        this.pageSchemeLineDTOS = pageSchemeLineDTOS;
    }
}
