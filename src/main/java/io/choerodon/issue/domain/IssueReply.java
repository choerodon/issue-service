package io.choerodon.issue.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */

@ModifyAudit
@VersionAudit
@Table(name = "issue_reply")
public class IssueReply extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private String content;
    private Long issueId;
    private Long sourceReplyId;
    private Long projectId;

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
