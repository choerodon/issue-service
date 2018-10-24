package script.db


databaseChangeLog(logicalFilePath: 'issue.groovy') {
    changeSet(id: '2018-08-02-create-issue', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: "issue") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'code', type: 'VARCHAR(32)', remarks: '编号')
            column(name: 'issue_type_id', type: 'BIGINT', remarks: '问题类型ID')
            column(name: 'subject', type: 'VARCHAR(256)', remarks: '主题')
            column(name: 'description', type: 'text', remarks: '描述')
            column(name: 'reporter_id', type: 'BIGINT', remarks: '提单人ID')
            column(name: 'handler_id', type: 'BIGINT', remarks: '处理人ID')
            column(name: 'priority_id', type: 'BIGINT', remarks: '优先级ID')
            column(name: 'status_id', type: 'BIGINT', remarks: '状态ID')
            column(name: 'handle_date', type: 'DATETIME', remarks: '处理时间')
            column(name: 'solve_date', type: 'DATETIME', remarks: '解决时间')
            column(name: 'issue_tag', type: 'VARCHAR(100)', remarks: '标签')
            column(name: 'project_id', type: 'BIGINT', remarks: '项目')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

}