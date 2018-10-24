package io.choerodon.issue.infra.enums;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/24
 */
public enum IssueTypeE {
    /**
     * 史诗
     */
    EPIC("icon", "史诗", "史诗", "colour", "epic"),
    /**
     * 故事
     */
    STORY("icon", "故事", "故事", "colour", "story"),
    /**
     * 缺陷
     */
    BUG("icon", "缺陷", "缺陷", "colour", "bug"),
    /**
     * 任务
     */
    TASK("icon", "任务", "任务", "colour", "task"),
    /**
     * 子任务
     */
    SUB_TASK("icon", "子任务", "子任务", "colour", "sub_task");

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
