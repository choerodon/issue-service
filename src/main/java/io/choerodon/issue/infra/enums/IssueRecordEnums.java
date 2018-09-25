package io.choerodon.issue.infra.enums;

/**
 * @author peng.jiang@hand-china.com
 */
public interface IssueRecordEnums {

    // 修改记录字段来源
    enum FieldSource {
        SYSTEM("system"),
        CUSTOM("custom"),
        REPLY("reply"),
        ATTACHMENT("attachment");
        private String value;
        FieldSource(String value) {
            this.value = value;
        }
        public String value() {
            return this.value;
        }
    }

    // 修改自定义字段，字段值的类型，options：需要去查表，获取真实值
    enum ValueType {
        OPTIONS("options");
        private String value;
        ValueType(String value) {
            this.value = value;
        }
        public String value() {
            return this.value;
        }
    }

    // 事件单动作
    enum IssueSystemAction {
        UPDATE_SUBJECT("updateSubject", "修改主题"),
        UPDATE_DESCRIPTION("updateDescription", "修改描述"),
        UPDATE_PRIORITY_ID("updatePriorityId", "修改优先级"),
        UPDATE_STATUS_ID("updateStatusId", "更新状态"),
        CREATE_ISSUE("creatIssue", "创建事件单"),
        UPDATE_HANDLER_ID("updateHandlerId", "修改处理人"),
        CREATE_HANDLER_ID("createHandlerId", "分派事件单");
        private String code;
        private String value;
        IssueSystemAction(String code, String value) {
            this.code = code;
            this.value = value;
        }
        public String code() {
            return this.code;
        }
        public String value() {
            return this.value;
        }
    }

    // 事件单自定义字段动作
    enum IssueCustomAction {
        UPDATE("update", "修改");//修改某个字段
        private String code;
        private String value;
        IssueCustomAction(String code, String value) {
            this.code = code;
            this.value = value;
        }
        public String code() {
            return this.code;
        }
        public String value() {
            return this.value;
        }
    }

    // 回复动作 reply
    enum ReplyAction {
        REPLY("reply", "回复事件单"),
        UPDATE_REPLY("updateReply", "修改回复"),
        DELETE_REPLY("deleteReply", "删除回复");
        private String code;
        private String value;
        ReplyAction(String code, String value) {
            this.code = code;
            this.value = value;
        }
        public String code() {
            return this.code;
        }
        public String value() {
            return this.value;
        }
    }

    // 附件动作
    enum AttachmentAction {
        UPLOAD_ATTACHMENT("uploadAttachment", "上传附件"),
        DELETE_ATTACHMENT("deleteAttachment", "删除附件");
        private String code;
        private String value;
        AttachmentAction(String code, String value) {
            this.code = code;
            this.value = value;
        }
        public String code() {
            return this.code;
        }
        public String value() {
            return this.value;
        }
    }

    // 修改记录的操作  新增/修改/删除
    enum IssueRecordOperationType {
        CREATE("create"),
        UPDATE("update"),
        DELETE("delete");
        private String value;
        IssueRecordOperationType(String value) {
            this.value = value;
        }
        public String value() {
            return this.value;
        }
    }

}
