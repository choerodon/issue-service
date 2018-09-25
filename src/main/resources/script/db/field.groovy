package script.db


databaseChangeLog(logicalFilePath: 'field.groovy') {

    changeSet(id: '2018-08-20-add-table-field', author: 'peng.jiang') {
        createTable(tableName: "field") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称')
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'type', type: 'VARCHAR(20)', remarks: '类型')
            column(name: 'default_value', type: 'VARCHAR(64)', remarks: '默认值')
            column(name: 'extra_config', type: 'VARCHAR(64)', remarks: '额外配置')
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "field", indexName: "field_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "field", indexName: "field_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "field", indexName: "field_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-20-add-table-field-option', author: 'peng.jiang') {
        createTable(tableName: "field_option") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '自定义字段id')
            column(name: 'value', type: 'VARCHAR(255)', remarks: '字段值')
            column(name: 'parent_id', type: 'BIGINT UNSIGNED', remarks: '父id')
            column(name: 'sequence', type: 'DECIMAL', remarks: '序号')
            column(name: 'is_enable', type: 'CHAR(1)', defaultValue: "1", remarks: '是否启用')
            column(name: 'is_default', type: 'CHAR(1)', defaultValue: "0", remarks: '是否默认')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "field_option", indexName: "field_option_n1") {
            column(name: "field_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "field_option", indexName: "field_option_n2") {
            column(name: "is_enable", type: "CHAR(1)")
        }
    }

    changeSet(id: '2018-08-20-add-table-field-config', author: 'peng.jiang') {
        createTable(tableName: "field_config") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称')
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "field_config", indexName: "field_config_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "field_config", indexName: "field_config_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "field_config", indexName: "field_config_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-20-add-table-field-config-line', author: 'peng.jiang') {
        createTable(tableName: "field_config_line") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'field_config_id', type: 'BIGINT UNSIGNED', remarks: '字段配置id')
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id')
            column(name: 'is_display', type: 'CHAR(1)', defaultValue: "0", remarks: '是否显示')
            column(name: 'is_required', type: 'CHAR(1)', defaultValue: "0", remarks: '是否必输')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "field_config_line", indexName: "field_config_line_n1") {
            column(name: "field_config_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "field_config_line", indexName: "field_config_line_n2") {
            column(name: "field_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-20-add-table-field-config-scheme', author: 'peng.jiang') {
        createTable(tableName: "field_config_scheme") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称')
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "field_config_scheme", indexName: "field_config_scheme_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "field_config_scheme", indexName: "field_config_scheme_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "field_config_scheme", indexName: "field_config_scheme_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-20-add-table-field-config-scheme-line', author: 'peng.jiang') {
        createTable(tableName: "field_config_scheme_line") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'scheme_id', type: 'BIGINT UNSIGNED', remarks: '字段配置方案id')
            column(name: 'issue_type_id', type: 'BIGINT UNSIGNED', remarks: '问题类型id')
            column(name: 'field_config_id', type: 'BIGINT UNSIGNED', remarks: '字段配置id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "field_config_scheme_line", indexName: "field_config_scheme_line_n1") {
            column(name: "scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "field_config_scheme_line", indexName: "field_config_scheme_line_n2") {
            column(name: "issue_type_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "field_config_scheme_line", indexName: "field_config_scheme_line_n3") {
            column(name: "field_config_id", type: "BIGINT UNSIGNED")
        }
    }

}