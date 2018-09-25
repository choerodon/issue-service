package script.db


databaseChangeLog(logicalFilePath: 'attachment.groovy') {

    changeSet(id: '2018-09-03-add-table-attachment', author: 'jiameng.cao') {
        createTable(tableName: "attachment") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'resource_type', type: 'VARCHAR(20)', remarks: '资源类型')
            column(name: 'resource_id', type: 'BIGINT UNSIGNED', remarks: '资源ID')
            column(name: 'file_name', type: 'VARCHAR(64)', remarks: '文件名')
            column(name: 'file_url', type: 'VARCHAR(255)', remarks: '文件存储地址')
            column(name: 'file_size', type: 'BIGINT UNSIGNED', remarks: '文件大小')
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: '用户ID')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目ID')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "attachment", indexName: "attachment_n1") {
            column(name: "user_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "attachment", indexName: "attachment_n2") {
            column(name: "resource_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "attachment", indexName: "attachment_n3") {
            column(name: "file_name", type: "VARCHAR(64)")
        }
    }
}