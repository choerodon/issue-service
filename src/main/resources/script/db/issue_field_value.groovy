package script.db


databaseChangeLog(logicalFilePath: 'issue_field_value.groovy') {
    changeSet(id: '2018-09-04-create-issue-field-value', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'issue_field_value') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '问题id') {
                constraints(nullable: 'false')
            }
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id') {
                constraints(nullable: 'false')
            }
            column(name: 'field_value', type: 'VARCHAR(255)', remarks: '字段值'){
                constraints(nullable: 'false')
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "issue_field_value", indexName: "issue_field_value_n1") {
            column(name: "issue_id", type: "VARCHAR(64)")
        }
        createIndex(tableName: "issue_field_value", indexName: "issue_field_value_n2") {
            column(name: "field_id", type: "VARCHAR(255)")
        }
        createIndex(tableName: "issue_field_value", indexName: "issue_field_value_n3") {
            column(name: "field_value", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "issue_field_value", indexName: "issue_field_value_n4") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
    }
}