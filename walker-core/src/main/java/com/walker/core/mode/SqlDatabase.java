package com.walker.core.mode;


import com.google.common.collect.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
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
	List<Table> databaseTables = new ArrayList<>();

}




