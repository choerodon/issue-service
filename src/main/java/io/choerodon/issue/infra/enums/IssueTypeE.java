package io.choerodon.issue.infra.enums;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/24
 */
public enum IssueTypeE {
    /**
     * 史诗
     */
    EPIC("priority", "史诗", "史诗", "#743be7", "issue_epic", SchemeType.AGILE),
    /**
     * 故事
     */
    STORY("book", "故事", "故事", "#00bfa5", "story", SchemeType.AGILE),
    /**
     * 缺陷
     */
    BUG("bug_report", "缺陷", "缺陷", "#f44336", "bug", SchemeType.AGILE),
    /**
     * 任务
     */
    TASK("check", "任务", "任务", "#4d90fe", "task", SchemeType.AGILE),
    /**
     * 子任务
     */
    SUB_TASK("relation", "子任务", "子任务", "#4d90fe", "sub_task", SchemeType.AGILE),
    /**
     * 测试
     */
    ISSUE_TEST("test", "测试", "测试", "FFB103", "issue_test", SchemeType.TEST);

    private String icon;
    private String name;
    private String description;
    private String colour;
    private String typeCode;
    /**
     * 用于方案类型
     */
    private String schemeType;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    IssueTypeE(String icon, String name, String description, String colour, String typeCode, String schemeType) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.colour = colour;
        this.typeCode = typeCode;
        this.schemeType = schemeType;
    }
}
