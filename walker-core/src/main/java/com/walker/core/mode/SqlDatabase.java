package com.walker.core.mode;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class SqlDatabase implements Serializable {

	/**
	 * 数据库 或 用户 名 WALKER  WALKER_1
	 */
	String databaseName;
	/**
	 * 中文名
	 */
	String databaseNameChinese;

	/**
	 * 表列表
	 */
	List<SqlTable> databaseTables = new ArrayList<>();

}




