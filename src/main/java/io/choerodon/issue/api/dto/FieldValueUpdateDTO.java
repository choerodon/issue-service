package io.choerodon.issue.api.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author shinan.chen
 * @since 2019/4/8
 */
public class FieldValueUpdateDTO extends BaseDTO {
    @ApiModelProperty(value = "字段选项id")
    private Long optionId;
    @ApiModelProperty(value = "字符串值")
    private String stringValue;
    @ApiModelProperty(value = "数值")
    private String numberValue;
    @ApiModelProperty(value = "文本值")
    private String textValue;
    @ApiModelProperty(value = "时间值")
    private Date dateValue;

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(String numberValue) {
        this.numberValue = numberValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
}
