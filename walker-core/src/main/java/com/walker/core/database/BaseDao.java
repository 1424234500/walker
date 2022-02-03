package com.walker.core.database;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.walker.mode.Page;
import com.walker.mode.SqlColumn;
import com.walker.mode.SqlDatabase;
import com.walker.mode.SqlTable;

import java.util.List;
import java.util.Map;

/**
 * 基础数据库操作类
 * 
 * 统一使用Map<String, Object>对象
 * 
 * @author
 * 
 */
public interface BaseDao extends BaseJdbc{


	/**
	 *获取 mysql数据库列表 oracle用户列表
	 */
	List<SqlDatabase> getDatabasesOrUsers();
	/**
	 *获取指定用户/db的表列表
	 * @param dbOrUser oracle用户 or mysql数据库db	为空默认当前
	 */
	List<SqlTable> getTables(String dbOrUser);

	/**
	 * 获取表的列
	 * @param dbOrUser db 或 用户 为空默认当前
	 * @param tableName
	 */
	List<SqlColumn> getColumnsByDbAndTable(String dbOrUser, String tableName);


	/**
	 * 获取单条记录
	 * 
	 * @param sql
	 * @param params
	 */
	Map<String, Object> findOne(String sql, Object... params);

	default <T> T findOneObj(String sql, Object... params){
		Map<String, Object> findOne = findOne(sql, params);
		T res = null;
		if(findOne != null) {
			res = JSON.parseObject(JSON.toJSONString(findOne), new TypeReference<T>() {});
		}
		return res;
	}


	/**
	 * 获得结果集
	 * 
	 * @param sql    SQL语句
	 * @param params 参数
	 * @param page   要显示第几页
	 * @param rows   每页显示多少条
	 * @return 结果集
	 */
	List<Map<String, Object>> findPage(String sql, Integer page, Integer rows, Object... params);

	default  <T> List<T> findPageObj(String sql, Integer page, Integer rows, Object... objects) {
		sql = SqlUtil.makeSqlPage(getDs(), sql, page, rows);
		return JSON.parseObject(JSON.toJSONString(this.find(sql, objects)), new TypeReference<List<T>>() {});
	}

	/**
	 * 获得结果集 分页
	 *
	 * @param sql    SQL语句
	 * @param params 参数
	 * @param page   分页对象 排序
	 * @return 结果集
	 */
	List<Map<String, Object>> findPage(Page page, String sql, Object... params);
	default <T> List<T> findPageObj(Page page, String sql, Object... objects) {
		return JSON.parseObject(JSON.toJSONString(this.findPage(sql, page.getNowpage(), page.getShownum(), objects)), new TypeReference<List<T>>() {});
	}

	/**
	 * 获得结果集 随机 分页
	 *
	 * @param sql    SQL语句
	 * @param params 参数
	 * @return 结果集
	 */
	List<Map<String, Object>> findPageRand(int size, String sql, Object... params);
	default <T> List<T> findPageRandObj(int size, String sql, Object... objects) {
		sql = SqlUtil.makeSqlPageRand(getDs(), sql, size);
		return JSON.parseObject(JSON.toJSONString(this.find(sql, objects)), new TypeReference<List<T>>() {});
	}
	/**
	 * 统计
	 * 
	 * @param sql    SQL语句
	 * @param params 参数
	 * @return 数目
	 */
	Integer count(String sql, Object... params);


}
