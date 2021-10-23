package com.walker.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

/**
 * 连接接口
 * 抽象出 共用的 配置读取加载
 *
 * @author walker
 */
abstract class Pool {
    /**
     * 默认第一个作为默认数据源
     */
    protected static List<String> defaultDsNames = Arrays.asList("mysql", "oracle");
    protected static String defaultDsName = defaultDsNames.get(0);
    Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 获取默认数据源 连接
     */
    abstract Connection getConn();

    abstract void close(Connection conn, PreparedStatement pst, ResultSet rs);

}
