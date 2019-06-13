package io.choerodon.issue.api.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author shinan.chen
 * @date 2018/9/7
 */
public class ProjectDTO {
    private static final String CODE_REGULAR_EXPRESSION = "[a-zA-Z0-9_\\.][a-zA-Z0-9_\\-\\.]*[a-zA-Z0-9_\\-]|[a-zA-Z0-9_]";
    @ApiModelProperty(value = "项目id")
    private Long id;
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "error.project.name.empty")
    @Size(min = 1, max = 32, message = "error.project.code.size")
    private String name;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "项目编码")
    @NotEmpty(message = "error.project.code.empty")
    @Size(min = 1, max = 14, message = "error.project.code.size")
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.project.code.illegal")
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
