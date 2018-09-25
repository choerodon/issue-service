package io.choerodon.issue.api.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/22
 */
public class PageDetailDTO {
    private Long id;

    @NotNull(message = "error.name.null")
    private String name;
    private String description;
    private Long organizationId;

    private Long objectVersionNumber;

    private List<FieldDTO> fieldDTOs;

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

    public List<FieldDTO> getFieldDTOs() {
        return fieldDTOs;
    }

    public void setFieldDTOs(List<FieldDTO> fieldDTOs) {
        this.fieldDTOs = fieldDTOs;
    }
}
