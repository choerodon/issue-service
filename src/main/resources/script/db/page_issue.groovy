package script.db


databaseChangeLog(logicalFilePath: 'page_issue.groovy') {
    changeSet(id: '2018-08-27-add-table-page_issue_scheme', author: 'peng.jiang') {
        createTable(tableName: "page_issue_scheme") {
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
        createIndex(tableName: "page_issue_scheme", indexName: "page_issue_scheme_n1") {
            column(name: "name", type: "VARCHAR(64)")
        }
        createIndex(tableName: "page_issue_scheme", indexName: "page_issue_scheme_n2") {
            column(name: "description", type: "VARCHAR(255)")
        }
        createIndex(tableName: "page_issue_scheme", indexName: "page_issue_scheme_n3") {
            column(name: "organization_id", type: "BIGINT UNSIGNED")
        }
    }

    changeSet(id: '2018-08-27-add-table-page_issue_scheme_line', author: 'peng.jiang') {
        createTable(tableName: "page_issue_scheme_line") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'scheme_id', type: 'BIGINT UNSIGNED', remarks: '问题类型页面方案id')
            column(name: 'issue_type_id', type: 'BIGINT UNSIGNED', remarks: '问题类型id')
            column(name: 'page_scheme_id', type: 'BIGINT UNSIGNED', remarks: '页面方案id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "page_issue_scheme_line", indexName: "page_issue_scheme_line_n1") {
            column(name: "scheme_id", type: "BIGINT UNSIGNED")
        }
    }
}


