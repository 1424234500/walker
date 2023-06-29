package com.walker.core.database.walkerjdbc;

import com.walker.core.annotation.WalkerJdbc;
import com.walker.core.annotation.WalkerJdbcQuery;
import com.walker.core.mode.Page;

import java.util.List;
import java.util.Map;

/**
 * 模拟mybatis 动态代理实现接口 mapper
 */
@WalkerJdbc("studentWalkerJdbc")
public interface StudentWalkerJdbc {

	/**
	 * 查询 page开头 必须分页
	 */
	@WalkerJdbcQuery("select * from W_STUDENT where name like ? ")
	List<Map<String, Object>> find(Page page, String name);

	/**
	 * sql执行
	 *
	 * @param id
	 * @param name
	 * @return 操作数量
	 */
	@WalkerJdbcQuery("update W_STUDENT set NAME=? where ID=? ")
	Integer updateNameById(String name, String id);

}
