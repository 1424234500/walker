package com.walker.core.database;

import java.util.List;
import java.util.Map;

public interface BaseJdbc {
    int MAX_SIZE_TABLE = 998;

    String getDs();

    /////////////////////////////////必须实现 其他依赖以下接口

    /**
     * 获得结果集
     *
     * @param sql    SQL语句
     * @param params 参数
     * @return 结果集
     */
    List<Map<String, Object>> find(String sql, Object... params);

    default List<Map<String, Object>> find(String sql, List<Object> params) {
        return find(sql, params.toArray());
    }

    /**
     * 执行SQL语句
     *
     * @return 响应行数
     */
    Integer executeSql(String sql, Object... params);

    /**
     * 批量执行sql
     */
    Integer[] executeSqlBatch(String sql, List<List<Object>> objs);

    /**
     * 执行存储过程 最后一个占位?返回值
     *
     * @param proc    "{call countBySal(?,?)}"
     * @param objects
     */
    Integer executeProc(String proc, Object... objects);
}
