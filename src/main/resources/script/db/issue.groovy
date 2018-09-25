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

    changeSet(id: '2018-08-08-create-issue-type', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'issue_type') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')

            column(name: 'icon', type: 'VARCHAR(64)', remarks: '图标')

            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "issue_type", indexName: "issue_type_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "issue_type", indexName: "issue_type_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "issue_type", indexName: "issue_type_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-10-create-issue-type-scheme', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'issue_type_scheme') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')

            column(name: 'default_issue_type_id', type: 'BIGINT UNSIGNED', remarks: '默认问题类型id') {
                constraints(nullable: 'false')
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "issue_type_scheme", indexName: "issue_type_scheme_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "issue_type_scheme", indexName: "issue_type_scheme_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "issue_type_scheme", indexName: "issue_type_scheme_n3") {
            column(name: "default_issue_type_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "issue_type_scheme", indexName: "issue_type_scheme_n4") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-10-create-issue-type-scheme-config', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'issue_type_scheme_config') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'scheme_id', type: 'BIGINT UNSIGNED', remarks: '问题方案id') {
                constraints(nullable: 'false')
            }
            column(name: 'issue_type_id', type: 'BIGINT UNSIGNED', remarks: '问题类型id') {
                constraints(nullable: 'false')
            }
            column(name: 'sequence', type: 'DECIMAL', defaultValue: "0", remarks: '排序'){
                constraints(nullable: 'false')
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "issue_type_scheme_config", indexName: "issue_type_scheme_config_n1") {
            column(name: "scheme_id", type: "VARCHAR(64)")
        }
        createIndex(tableName: "issue_type_scheme_config", indexName: "issue_type_scheme_config_n2") {
            column(name: "issue_type_id", type: "VARCHAR(255)")
        }
        createIndex(tableName: "issue_type_scheme_config", indexName: "issue_type_scheme_config_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

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