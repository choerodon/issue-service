package io.choerodon.issue.infra.enums;

/**
 * @author jiameng.cao
 * @date 2018/9/4
 */
public enum ResourceType {
    ISSUE("issue"),
    REPLY("reply");

    private String typeName;

    ResourceType(String typeName) {
        this.typeName = typeName;
    }

    public String value() {
        return this.typeName;
    }

    public static Boolean contain(String typeName) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.value().equals(typeName)) {
                return true;
            }
        }
        return false;
    }
}
