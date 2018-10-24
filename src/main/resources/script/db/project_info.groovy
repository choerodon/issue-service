package script.db


databaseChangeLog(logicalFilePath: 'project_info.groovy') {
    changeSet(id: '2018-09-10-create-project-info', author: 'shinan.chenX@gmail.com') {
        createTable(tableName: 'project_info') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: '主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: 'false')
            }
            column(name: 'project_code', type: 'VARCHAR(255)', remarks: '项目code') {
                constraints(nullable: 'false')
            }
            column(name: 'issue_max_num', type: 'BIGINT UNSIGNED', remarks: 'issue编号最大值') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "project_info", indexName: "project_info_n1") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "project_info", indexName: "project_info_n2") {
            column(name: "project_code", type: "VARCHAR(255)")
        }
    }
}