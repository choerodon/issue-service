package io.choerodon.issue.api.dto;


import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/28
 */
public class FieldOptionDTO {
    private Long id;
    @NotNull(message = "error.fieldId.null")
    private Long fieldId;
    @NotNull(message = "error.value.null")
    private String value;
    @NotNull(message = "error.parentId.null")
    private Long parentId;//若为父级则为0L
    private BigDecimal sequence;
    private String isEnable;
    private String isDefault;
    private Long objectVersionNumber;

    private List<FieldOptionDTO> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public List<FieldOptionDTO> getChildren() {
        return children;
    }

    public void setChildren(List<FieldOptionDTO> children) {
        this.children = children;
    }
}
