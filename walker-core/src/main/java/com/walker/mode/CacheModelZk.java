package com.walker.mode;


import lombok.Data;

@Data
public class CacheModelZk extends CacheModel {
	int CHILD_SIZE;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("CHILD_SIZE");
		column.setColumnNameChinese("子节点数");
		colMaps.add(column);
	}
	String ACL;
	static {
		SqlColumn column = new SqlColumn();
		column.setColumnName("ACL");
		column.setColumnNameChinese("权限");
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
