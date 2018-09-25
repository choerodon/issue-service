package script.db


databaseChangeLog(logicalFilePath: 'issue_record.groovy') {
    changeSet(id: '2018-09-03-add-table-issue_record', author: 'peng.jiang') {

        createTable(tableName: "issue_record") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'group_id', type: 'BIGINT UNSIGNED', remarks: '组id')
            column(name: 'field_source', type: 'VARCHAR(64)', remarks: '字段来源')
            column(name: 'field_name', type: 'VARCHAR(64)', remarks: '字段名称')
            column(name: 'old_id', type: 'BIGINT UNSIGNED', remarks: '旧值id')
            column(name: 'old_value', type: 'VARCHAR(2000)', remarks: '旧值')
            column(name: 'new_id', type: 'BIGINT UNSIGNED', remarks: '新值id')
            column(name: 'new_value', type: 'VARCHAR(2000)', remarks: '新值')
            column(name: 'value_type', type: 'VARCHAR(64)', remarks: '值类型,值为id,需要查表获取真正的值')
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '问题id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: "issue_record", indexName: "issue_record_n1") {
            column(name: "group_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "issue_record", indexName: "issue_record_n2") {
            column(name: "issue_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "issue_record", indexName: "issue_record_n3") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
    }
}


