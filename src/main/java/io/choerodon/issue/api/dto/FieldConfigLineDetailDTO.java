package io.choerodon.issue.api.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/9/12
 */
public class FieldConfigLineDetailDTO {
    private Long id;
    private Long fieldConfigId;
    private Long fieldId;
    @NotNull(message = "error.isDisplay.null")
    private String isDisplay;
    @NotNull(message = "error.isRequired.null")
    private String isRequired;
    private Long objectVersionNumber;
    private String fieldName;
    private String fieldDescription;

    private List<PageDTO> pageDTOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFieldConfigId() {
        return fieldConfigId;
    }

    public void setFieldConfigId(Long fieldConfigId) {
        this.fieldConfigId = fieldConfigId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(String isDisplay) {
        this.isDisplay = isDisplay;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<PageDTO> getPageDTOList() {
        return pageDTOList;
    }

    public void setPageDTOList(List<PageDTO> pageDTOList) {
        this.pageDTOList = pageDTOList;
    }
}
