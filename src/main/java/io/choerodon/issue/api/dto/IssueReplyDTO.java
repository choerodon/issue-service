package io.choerodon.issue.api.dto;

import javax.validation.constraints.NotNull;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */
public class IssueReplyDTO {
    private Long id;

    @NotNull(message = "error.userId.null")
    private Long userId;
    @NotNull(message = "error.content.null")
    private String content;
    @NotNull(message = "error.issueId.null")
    private Long issueId;
    @NotNull(message = "error.sourceReplyId.null")
    private Long sourceReplyId;
    private Long projectId;

    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }


    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getSourceReplyId() {
        return sourceReplyId;
    }

    public void setSourceReplyId(Long sourceReplyId) {
        this.sourceReplyId = sourceReplyId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
