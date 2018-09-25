package io.choerodon.issue.infra.enums;

/**
 * @author jiameng.cao
 * @date 2018/9/12
 */
public enum FieldNameE {
    EPIC("epic",FieldType.SINGLE.value()),
    SPRINT("sprint",FieldType.SINGLE.value()),
    SOLUTION("solution",FieldType.TEXT.value());

    private String typeName;
    private String type;

    FieldNameE(String typeName,String type) {
        this.typeName = typeName;
        this.type=type;
    }

    public String value() {
        return this.typeName;
    }

    public String type(){
        return this.type;
    }
}
