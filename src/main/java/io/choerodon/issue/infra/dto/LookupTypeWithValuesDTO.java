package io.choerodon.issue.infra.dto;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2019/7/9
 */
public class LookupTypeWithValuesDTO {

    private String typeCode;

    private String name;

    private String description;

    private Long objectVersionNumber;

    private List<LookupValueDTO> lookupValues;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<LookupValueDTO> getLookupValues() {
        return lookupValues;
    }

    public void setLookupValues(List<LookupValueDTO> lookupValues) {
        this.lookupValues = lookupValues;
    }
}
