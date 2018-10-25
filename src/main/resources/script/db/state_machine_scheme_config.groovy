package script.db


databaseChangeLog(logicalFilePath: 'state_machine_scheme_config.groovy') {
    changeSet(id: '2018-08-07-add-table-state-machine-scheme-config', author: 'shinan.chenX@gmail') {
        createTable(tableName: "state_machine_scheme_config") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID') {
                constraints(primaryKey: true)
            }
            column(name: 'scheme_id', type: 'BIGINT UNSIGNED', remarks: '方案ID')
            column(name: 'issue_type_id', type: 'BIGINT UNSIGNED', remarks: '问题类型ID')
            column(name: 'state_machine_id', type: 'BIGINT UNSIGNED', remarks: '状态机Id')
            column(name: 'sequence', type: 'decimal', remarks: '排序')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: "state_machine_scheme_config", indexName: "state_machine_scheme_config_n1") {
            column(name: "scheme_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "state_machine_scheme_config", indexName: "state_machine_scheme_config_n2") {
            column(name: "issue_type_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "state_machine_scheme_config", indexName: "state_machine_scheme_config_n3") {
            column(name: "state_machine_id", type: "BIGINT UNSIGNED")
        }
    }
}