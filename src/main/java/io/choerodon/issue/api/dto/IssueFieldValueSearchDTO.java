package io.choerodon.issue.api.dto;


/**
 * 搜索类
 *
 * @author shinan.chen
 * @date 2018/9/12
 */
public class IssueFieldValueSearchDTO {

    private Long fieldId;
    private String fieldValue;
    private String fieldType;

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

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}
