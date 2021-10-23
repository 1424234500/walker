package com.walker.core.annotation;

/**
 * 注解使用样例
 */
@DBTable(name = "EG_DB_TABLE")
public class EgDBTable {

    @DBSQLString(value = 256, name = "ID", DBConstraints = @DBConstraints(primaryKey = true))
    String id;


    @DBSQLString(name = "NAME", DBConstraints = @DBConstraints(allowNull = true, unique = true))
    String name;

    @DBSQLInteger
    String age;


}