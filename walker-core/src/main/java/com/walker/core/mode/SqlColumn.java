package com.walker.core.mode;

import lombok.Data;

/**
 * create table W_HELLO(TEST_ID VARCHAR(20) not null , primary key(TEST_ID)) comment TEST_ID 测试ID
 */
@Data
public class SqlColumn {

	/**
	 * table 字段 TEST_ID
	 */
	String columnName;
	/**
	 * table 字段类型 VARCHAR(20)
	 */
	String columnType = "VARCHAR(20)";
	/**
	 * 字段中文名 测试ID
	 */
	String columnNameChinese;
	/**
	 * 字段默认值 hello
	 */
	String columnDefaultValue;

	//	索引约束
	boolean nullable;
	boolean primaryKey;

}
