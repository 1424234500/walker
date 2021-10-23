package com.walker.core.mode;


import lombok.Data;

@Data
public class CacheModelRedis extends CacheModel {
	Long TTL;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("TTL");
		column.setColumnNameChinese("ttl");
		colMaps.add(column);
	}
	String EXISTS;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("EXISTS");
		column.setColumnNameChinese("EXISTS");
		colMaps.add(column);
	}
	String STAT;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("STAT");
		column.setColumnNameChinese("状态");
		colMaps.add(column);
	}

}
