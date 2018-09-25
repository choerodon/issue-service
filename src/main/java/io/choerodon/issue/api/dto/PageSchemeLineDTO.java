package io.choerodon.issue.api.dto;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @date 2018/8/23
 */
public class PageSchemeLineDTO {
    private Long id;

    @NotNull(message = "error.pageId.null")
    private Long pageId;
    @NotNull(message = "error.schemeId.null")
    private Long schemeId;
    @NotNull(message = "error.type.null")
    private String type;
    private Long organizationId;

    private Long objectVersionNumber;

    @Transient
    private String pageName;

    @Transient
    private Boolean isDefault = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Long getPageId() {
        return pageId;
    }

    public void setPageId(@NotNull Long pageId) {
        this.pageId = pageId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
