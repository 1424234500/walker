package com.walker.core.database;

import com.walker.mode.Page;
import com.walker.mode.SqlColumn;
import com.walker.mode.SqlDatabase;
import com.walker.mode.SqlTable;
import com.walker.util.MapListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据库常用操作工具 选择一种连接池实现 每种连接池对应多种数据源 多种数据库
 * <p>
 * jdbc 一个实例 绑定 连接池 和 数据源 但 底层 是同一个连接池 且 每个连接池每个 数据源是 唯一
 * <p>
 * 部分实现 抽离关键 子实现
 */
public abstract class BaseDaoAdapter implements BaseDao {

	public Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 获取 mysql数据库列表 oracle用户列表
	 */
	public List<SqlDatabase> getDatabasesOrUsers() {
		List<Object> args = new ArrayList<>();
		String sql = "";
		if (getDs().equals("mysql")) {
			sql = "SELECT schema_name databaseName, schema_name databaseNameChinese FROM INFORMATION_SCHEMA.SCHEMATA ";
		} else if (getDs().equals("oracle")) {
			sql = " select username databaseName, username databaseNameChinese name from all_users ";
		} else {
			throw new RuntimeException("no implements  " + getDs());
		}

		return this.findPageObj(new Page().setPagenum(MAX_SIZE_TABLE), sql);
	}

	/**
	 * 获取指定用户/db的表列表
	 *
	 * @param dbOrUser oracle用户 or mysql数据库db	为空默认当前
	 */
	public List<SqlTable> getTables(String dbOrUser) {
		List<SqlTable> res = null;

		String dsName = getDs();
		if (dsName.equals("mysql")) {
			if (dbOrUser.length() > 0) {
				res = this.findPageObj(new Page().setPagenum(MAX_SIZE_TABLE), "SELECT table_name tableName, table_name tableNameChinese FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA =? ", dbOrUser);
			} else {
				res = this.findPageObj(new Page().setPagenum(MAX_SIZE_TABLE), "SELECT table_name tableName, table_name tableNameChinese FROM INFORMATION_SCHEMA.TABLES   ");
			}
		} else if (dsName.equals("oracle")) {
			if (dbOrUser.length() > 0) {
				res = this.findPageObj(new Page().setPagenum(MAX_SIZE_TABLE), "select table_name tableName, table_name tableNameChinese from all_tab_comments where owner=? ", dbOrUser);
			} else {
				res = this.findPageObj(new Page().setPagenum(MAX_SIZE_TABLE), "select table_name tableName, table_name tableNameChinese from user_tab_comments");
			}
		} else {
			throw new RuntimeException("no implements  " + dsName);
		}

		return res;
	}

	/**
	 * 获取表的列
	 *
	 * @param dbOrUser  db 或 用户 为空默认当前
	 * @param tableName
	 */
	public List<SqlColumn> getColumnsByDbAndTable(String dbOrUser, String tableName) {
		List<SqlColumn> list = null;
		String sql = "";
		List<Object> args = new ArrayList<>();
		if (getDs().equals("mysql")) {

			sql = "select COLUMN_NAME columnName, COLUMN_COMMENT columnNameChinese, data_type columnType, column_default columnDefaultValue, is_nullable nullable from INFORMATION_SCHEMA.COLUMNS where 1=1 ";

			if (dbOrUser.length() > 0) {
				sql += " and table_schema=? ";
				args.add(dbOrUser);
			}
			if (tableName.length() > 0) {
				sql += " and table_name=? ";
				args.add(tableName);
			}
			sql += "order by ORDINAL_POSITION";

			list = this.findPageObj(new Page().setPagenum(MAX_SIZE_TABLE), sql, args.toArray());

		} else if (getDs().equals("oracle")) {
			if (dbOrUser.length() > 0) {
				sql = "SELECT COLUMN_NAME columnName, COLUMN_NAME columnNameChinese FROM ALL_TAB_COLUMNS where 1=1 ";
				if (dbOrUser.length() > 0) {
					sql += " and owner=? ";
					args.add(tableName);
				}
			} else {
				sql = "SELECT COLUMN_NAME columnName, COLUMN_NAME columnNameChinese FROM USER_TAB_COLUMNS where 1=1 ";
			}
			if (tableName.length() > 0) {
				sql += " and TABLE_NAME=? ";
				args.add(tableName);
			}
			list = this.findPageObj(new Page().setPagenum(MAX_SIZE_TABLE), sql, args.toArray());
		} else {
			throw new RuntimeException("no implements  " + getDs());
		}

		return list;
	}


	@Override
	public Map<String, Object> findOne(String sql, Object... objects) {
		List<Map<String, Object>> list = this.findPage(sql, 1, 1, objects);
		Map<String, Object> res = null;
		if (list.size() >= 1) {
			res = list.get(0);
		}
		return res;
	}

	/**
	 * 获得结果集
	 *
	 * @param sql  SQL语句
	 * @param page 要显示第几页
	 * @param rows 每页显示多少条
	 * @return 结果集
	 */
	@Override
	public List<Map<String, Object>> findPage(String sql, Integer page, Integer rows, Object... objects) {
		sql = SqlUtil.makeSqlPage(getDs(), sql, page, rows);
		return this.find(sql, objects);
	}

	@Override
	public List<Map<String, Object>> findPageRand(int size, String sql, Object... objects) {
		sql = SqlUtil.makeSqlPageRand(getDs(), sql, size);
		return this.find(sql, objects);
	}

	@Override
	public List<Map<String, Object>> findPage(Page page, String sql, Object... objects) {
		page.setNum(this.count(sql, objects));
		sql = SqlUtil.makeSqlOrder(sql, page.getOrder());
		return this.findPage(sql, page.getNowpage(), page.getShownum(), objects);
	}

	@Override
	public Integer count(String sql, Object... objects) {
		Integer res = 0;
		sql = SqlUtil.makeSqlCount(sql);
		List<List<String>> list = MapListUtil.toArray(this.find(sql, objects));
		if (list != null && list.size() > 0) {
			List<String> row = list.get(0);
			if (row != null && row.size() > 0) {
				res = Integer.valueOf(row.get(0));
			}
		}
		return res;
	}

}
