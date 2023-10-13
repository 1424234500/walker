package com.walker.core.mode;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class SqlTable implements Serializable {

	/**
	 * 表名字 W_HELLO_WORLD
	 */
	String tableName;
	/**
	 * 中文表名 你好表
	 */
	String tableNameChinese;

	/**
	 * 主键  {ID}
	 */
	SqlColumn tablePrimaryKey;

	/**
	 * 字段列表
	 */
	List<SqlColumn> tableColumns = new ArrayList<>();
}




