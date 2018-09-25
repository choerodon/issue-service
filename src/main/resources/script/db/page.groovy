package script.db


databaseChangeLog(logicalFilePath: 'page.groovy') {
    changeSet(id: '2018-08-22-create-page', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'page') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')

            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "page", indexName: "page_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "page", indexName: "page_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "page", indexName: "page_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-22-create-page-field-ref', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'page_field_ref') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'page_id', type: 'BIGINT UNSIGNED', remarks: '页面id') {
                constraints(nullable: 'false')
            }
            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id') {
                constraints(nullable: 'false')
            }
            column(name: 'sequence', type: 'DECIMAL', defaultValue: "0", remarks: '排序') {
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
        createIndex(tableName: "page_field_ref", indexName: "page_field_ref_n1") {
            column(name: "page_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "page_field_ref", indexName: "page_field_ref_n2") {
            column(name: "field_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "page_field_ref", indexName: "page_field_ref_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-23-create-page-scheme', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'page_scheme') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')

            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "page_scheme", indexName: "page_scheme_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "page_scheme", indexName: "page_scheme_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "page_scheme", indexName: "page_scheme_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-23-create-page-scheme-line', author: 'shinan.chen@hand-china.com') {
        createTable(tableName: 'page_scheme_line') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: 'true', remarks: 'ID,主键') {
                constraints(primaryKey: 'true')
            }
            column(name: 'page_id', type: 'BIGINT UNSIGNED', remarks: '页面id') {
                constraints(nullable: 'false')
            }
            column(name: 'scheme_id', type: 'BIGINT UNSIGNED', remarks: '页面方案id') {
                constraints(nullable: 'false')
            }
            column(name: 'type', type: 'VARCHAR(20)', remarks: '页面类型') {
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
        createIndex(tableName: "page_scheme_line", indexName: "page_scheme_line_n1") {
            column(name: "page_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "page_scheme_line", indexName: "page_scheme_line_n2") {
            column(name: "scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "page_scheme_line", indexName: "page_scheme_line_n3") {
            column(name: "type", type: "VARCHAR(20)")
        }
        createIndex(tableName: "page_scheme_line", indexName: "page_scheme_line_n4") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }
}