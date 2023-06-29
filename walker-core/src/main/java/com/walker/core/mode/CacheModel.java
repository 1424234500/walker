package com.walker.core.mode;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CacheModel implements Serializable {
	public static List<SqlColumn> colMaps = new ArrayList<>();

	String KEY;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("KEY");
		column.setColumnNameChinese("键");
		colMaps.add(column);
	}
	String HASHCODE;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("HASHCODE");
		column.setColumnNameChinese("hash");
		colMaps.add(column);
	}
	Object VALUE;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("VALUE");
		column.setColumnNameChinese("值");
		colMaps.add(column);
	}
	String MTIME;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("MTIME");
		column.setColumnNameChinese("修改时间");
		colMaps.add(column);
	}
    String EXPIRE;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("EXPIRE");
		column.setColumnNameChinese("过期时间");
		colMaps.add(column);
	}
    Long LEN;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("LEN");
		column.setColumnNameChinese("长度");
		colMaps.add(column);
	}
	String TYPE;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("TYPE");
		column.setColumnNameChinese("值类型");
		colMaps.add(column);
	}

	String COUNT = "命中次数";
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("COUNT");
		column.setColumnNameChinese("命中次数");
		colMaps.add(column);
	}

}
