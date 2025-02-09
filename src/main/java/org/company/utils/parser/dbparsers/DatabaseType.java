package org.company.utils.parser.dbparsers;

public enum DatabaseType {

    MYSQL(MySqlUrlParser.class);
    // to be expanded with support for other databases

    final Class<? extends DatabaseUrlParser> clazzType;

    public Class<? extends DatabaseUrlParser> getClazzType() {
        return clazzType;
    }
    DatabaseType(Class<? extends DatabaseUrlParser> clazz) {
        clazzType = clazz;
    }
}
