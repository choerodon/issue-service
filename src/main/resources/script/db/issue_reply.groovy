package script.db


databaseChangeLog(logicalFilePath: 'issue_reply.groovy') {

    changeSet(id: '2018-09-03-add-table-issue_reply', author: 'jiameng.cao') {
        createTable(tableName: "issue_reply") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: '用户ID')
            column(name: 'content', type: 'VARCHAR(255)', remarks: '内容')
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '问题id')
            column(name: 'source_reply_id', type: 'BIGINT UNSIGNED', remarks: '源回复ID')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目ID')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "issue_reply", indexName: "issue_reply_n1") {
            column(name: "user_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "issue_reply", indexName: "issue_reply_n2") {
            column(name: "issue_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "issue_reply", indexName: "issue_reply_n3") {
            column(name: "source_reply_id", type: "BIGINT UNSIGNED")
        }
    }
}