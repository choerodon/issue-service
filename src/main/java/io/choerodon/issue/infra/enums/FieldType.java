package io.choerodon.issue.infra.enums;

/**
 * @author shinan.chen
 * @date 2018/8/24
 */
public enum FieldType {

    TEXT("text"),
    RADIO("radio"),
    CHECKBOX("checkbox"),
    TIME("time"),
    DATETIME("datetime"),
    NUMBER("number"),
    INPUT("input"),
    SINGLE("single"),
    MULTIPLE("multiple"),
    CASCADE("cascade"),
    URL("url"),
    LABEL("label");

    private String typeName;

    FieldType(String typeName) {
        this.typeName = typeName;
    }

    public String value() {
        return this.typeName;
    }

    public static Boolean contain(String typeName) {
        for (FieldType fieldType : FieldType.values()) {
            if (fieldType.value().equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean hasOption(String typeName) {
        if (typeName.equals(RADIO.value()) || typeName.equals(CHECKBOX.value()) || typeName.equals(SINGLE.value()) || typeName.equals(MULTIPLE.value()) ||
                typeName.equals(CASCADE.value())){
            return true;
        }
        return false;
    }
}
