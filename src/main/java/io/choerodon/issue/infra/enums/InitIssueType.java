package io.choerodon.issue.infra.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/24
 */
public enum InitIssueType {
    /**
     * 史诗
     */
    EPIC("agile_epic", "史诗", "史诗", "#743be7", "issue_epic", SchemeApplyType.AGILE),
    /**
     * 故事
     */
    STORY("agile_story", "故事", "故事", "#00bfa5", "story", SchemeApplyType.AGILE),
    /**
     * 缺陷
     */
    BUG("agile_fault", "缺陷", "缺陷", "#f44336", "bug", SchemeApplyType.AGILE),
    /**
     * 任务
     */
    TASK("agile_task", "任务", "任务", "#4d90fe", "task", SchemeApplyType.AGILE),
    /**
     * 子任务
     */
    SUB_TASK("agile_subtask", "子任务", "子任务", "#4d90fe", "sub_task", SchemeApplyType.AGILE),
    /**
     * 测试
     */
    TEST("test", "测试", "测试", "#FFB103", "issue_test", SchemeApplyType.TEST);

    private String icon;
    private String name;
    private String description;
    private String colour;
    private String typeCode;
    /**
     * 方案应用类型
     */
    private String applyType;

    InitIssueType(String icon, String name, String description, String colour, String typeCode, String applyType) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.colour = colour;
        this.typeCode = typeCode;
        this.applyType = applyType;
    }

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

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public static List<InitIssueType> listByApplyType(String applyType){
        return Arrays.stream(InitIssueType.values()).filter(x->x.getApplyType().equals(applyType)).collect(Collectors.toList());
    }
}
