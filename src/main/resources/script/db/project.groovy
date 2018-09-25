package script.db


databaseChangeLog(logicalFilePath: 'project_config.groovy') {
    changeSet(id: '2018-09-10-create-project', author: 'shinan.chen@hand-china.com') {
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

    changeSet(id: '2018-09-04-create-project-config', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'project_config') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: '主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: 'false')
            }
            column(name: 'issue_type_scheme_id', type: 'BIGINT UNSIGNED', remarks: '问题类型方案id') {
                constraints(nullable: 'false')
            }
            column(name: 'field_config_scheme_id', type: 'BIGINT UNSIGNED', remarks: '字段配置方案id') {
                constraints(nullable: 'false')
            }
            column(name: 'page_issue_type_scheme_id', type: 'BIGINT UNSIGNED', remarks: '问题类型页面方案id') {
                constraints(nullable: 'false')
            }
            column(name: 'state_machine_scheme_id', type: 'BIGINT UNSIGNED', remarks: '状态机方案id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "project_config", indexName: "project_config_n1") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "project_config", indexName: "project_config_n2") {
            column(name: "issue_type_scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "project_config", indexName: "project_config_n3") {
            column(name: "field_config_scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "project_config", indexName: "project_config_n4") {
            column(name: "page_issue_type_scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "project_config", indexName: "project_config_n5") {
            column(name: "state_machine_scheme_id", type: "BIGINT UNSIGNED")
        }
    }
}