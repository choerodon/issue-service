package io.choerodon.issue.infra.enums;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/24
 */
public enum IssueTypeE {
    /**
     * 史诗
     */
    EPIC("priority", "史诗", "史诗", "#743be7", "epic"),
    /**
     * 故事
     */
    STORY("book", "故事", "故事", "#00bfa5", "story"),
    /**
     * 缺陷
     */
    BUG("bug_report", "缺陷", "缺陷", "#f44336", "bug"),
    /**
     * 任务
     */
    TASK("check", "任务", "任务", "#4d90fe", "task"),
    /**
     * 子任务
     */
    SUB_TASK("relation", "子任务", "子任务", "#4d90fe", "sub_task"),

    ISSUE_TEST("test", "测试", "测试", "FFB103", "issue_test");

    private String icon;
    private String name;
    private String description;
    private String colour;
    private String typeCode;

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColour() {
        return colour;
    }

    public String getTypeCode() {
        return typeCode;
    }

    IssueTypeE(String icon, String name, String description, String colour, String typeCode) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.colour = colour;
        this.typeCode = typeCode;
    }

}
