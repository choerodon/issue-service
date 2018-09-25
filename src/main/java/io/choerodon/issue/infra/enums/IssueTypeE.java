package io.choerodon.issue.infra.enums;

/**
 * @author jiameng.cao
 * @date 2018/9/11
 */
public enum IssueTypeE {
    EPIC("epic"),
    STORY("story"),
    BUG("bug"),
    TASK("task"),
    SUBTASK("subtask"),
    EVENT("event"),
    CHANGE("change"),
    SERVICE_REQUEST("service_request"),
    ITHELP("it_help"),
    Issue("issue");

    private String typeName;

    IssueTypeE(String typeName) {
        this.typeName = typeName;
    }

    public String value() {
        return this.typeName;
    }

    public static Boolean contain(String typeName) {
        for (IssueTypeE issueType : IssueTypeE.values()) {
            if (issueType.value().equals(typeName)) {
                return true;
            }
        }
        return false;
    }
}
