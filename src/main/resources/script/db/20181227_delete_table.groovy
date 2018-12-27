package script.db


databaseChangeLog(logicalFilePath: '20181227_delete_table.groovy') {
    changeSet(id: '2018-12-27-delete-old-table', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "DROP TABLE IF EXISTS attachment;" +
            "DROP TABLE IF EXISTS field;" +
            "DROP TABLE IF EXISTS field_option;" +
            "DROP TABLE IF EXISTS field_config;" +
            "DROP TABLE IF EXISTS field_config_line;" +
            "DROP TABLE IF EXISTS field_config_scheme;" +
            "DROP TABLE IF EXISTS field_config_scheme_line;" +
            "DROP TABLE IF EXISTS issue;" +
            "DROP TABLE IF EXISTS issue_field_value;" +
            "DROP TABLE IF EXISTS issue_record;" +
            "DROP TABLE IF EXISTS issue_reply;" +
            "DROP TABLE IF EXISTS page;" +
            "DROP TABLE IF EXISTS page_field_ref;" +
            "DROP TABLE IF EXISTS page_scheme;" +
            "DROP TABLE IF EXISTS page_scheme_line;" +
            "DROP TABLE IF EXISTS page_issue_scheme;" +
            "DROP TABLE IF EXISTS page_issue_scheme_line"
        }
    }
}