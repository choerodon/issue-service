package io.choerodon.issue.api.dto;


import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/27
 */
public class FieldDetailDTO {

    private Long id;
    @NotNull(message = "error.name.null")
    private String name;
    private String description;
    @NotNull(message = "error.type.null")
    private String type;
    private String defaultValue;
    private String extraConfig;
    private Long organizationId;
    private Long objectVersionNumber;

    private List<FieldOptionDTO> fieldOptions;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(String extraConfig) {
        this.extraConfig = extraConfig;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


    public List<FieldOptionDTO> getFieldOptions() {
        return fieldOptions;
    }

    public void setFieldOptions(List<FieldOptionDTO> fieldOptions) {
        this.fieldOptions = fieldOptions;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
