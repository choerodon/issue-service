package io.choerodon.issue.api.dto;

import java.util.Date;
import java.util.List;

/**
 * @author peng.jiang
 * @date 2018/9/4
 */
public class IssueRecordViewDTO {
    private Long id;
    private String operatorName; //操作人
    private String imageUrl; //操作人头像
    private String fieldSource;//修改的字段来源
    private Date creationDate;//记录产生时间
    private String action;//动作
    private List<AttachmentDTO> attachmentDTOS; //附件列表
    private List<IssueReplyDTO> replyDTOS; //回复
    private String oldValue; //事件单,修改前的值
    private String newValue; //事件单,修改后的值

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<AttachmentDTO> getAttachmentDTOS() {
        return attachmentDTOS;
    }

    public void setAttachmentDTOS(List<AttachmentDTO> attachmentDTOS) {
        this.attachmentDTOS = attachmentDTOS;
    }

    public List<IssueReplyDTO> getReplyDTOS() {
        return replyDTOS;
    }

    public void setReplyDTOS(List<IssueReplyDTO> replyDTOS) {
        this.replyDTOS = replyDTOS;
    }

    public String getFieldSource() {
        return fieldSource;
    }

    public void setFieldSource(String fieldSource) {
        this.fieldSource = fieldSource;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
