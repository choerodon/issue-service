package io.choerodon.issue.api.dto;


import java.util.List;

/**
 * 该类用于创建问题及查看问题详情的字段展示
 *
 * @author shinan.chen
 * @date 2018/9/5
 */
public class IssueFieldValueDTO {

    private Long id;

    private Long issueId;
    private Long fieldId;
    private String fieldValue;
    private String fieldType;
    private String fieldName;
    private String fieldExtraConfig;
    private String isDisplay;
    private String isRequired;

    private Long objectVersionNumber;

    private List<FieldOptionDTO> fieldOptions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
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

    public String getFieldExtraConfig() {
        return fieldExtraConfig;
    }

    public void setFieldExtraConfig(String fieldExtraConfig) {
        this.fieldExtraConfig = fieldExtraConfig;
    }
}
