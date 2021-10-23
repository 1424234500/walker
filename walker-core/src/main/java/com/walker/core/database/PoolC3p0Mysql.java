package com.walker.core.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.walker.core.Context;
import com.walker.core.cache.ConfigMgr;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * redis 普通模式
 * 接入工具模型
 * CacheLockerBase
 * is a TestAdapter, cache, cacheDb, locker, lockerHelp
 * <p>
 * eg: *test
 */
@Data
public class PoolC3p0Mysql extends Pool {
    private final static Logger log = LoggerFactory.getLogger(PoolC3p0Mysql.class);
    String driver;
    String url;
    String user;
    String pwd;
    private ComboPooledDataSource ds;

    /**
     * 单例模式
     */
    public static PoolC3p0Mysql getInstance() {
        return SingletonFactory.instance;
    }

    @Override
    public Connection getConn() {
        try {
            if (ds == null) { //双重锁迟加载源
                synchronized (this) {
                    if (ds == null) {
                        ds = new ComboPooledDataSource();
                        try {

                            ds.setDriverClass(driver);
                            ds.setJdbcUrl(url);
                            ds.setUser(user);
                            ds.setPassword(pwd);
                        } catch (PropertyVetoException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
            return ds.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("数据库连接出错!" + ds + " " + e.getMessage(), e);
        }
    }

    @Override
    public void close(Connection conn, PreparedStatement pst, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException("数据库连接关闭出错!" + ds + " " + e.getMessage(), e);
            }
        }
        if (pst != null) {
            try {
                pst.close();
            } catch (SQLException e) {
                log.error("Exception in C3p0Utils! PreparedStatement", e);
                throw new RuntimeException("数据库连接关闭出错!" + ds + " " + e.getMessage(), e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("Exception in C3p0Utils! Connection", e);
                throw new RuntimeException("数据库连接关闭出错!" + ds + " " + e.getMessage(), e);
            }
        }
    }

    private static class SingletonFactory {
        static PoolC3p0Mysql instance;

        static {
            System.setProperty("com.mchange.v2.c3p0.cfg.xml", Context.getPathConf("c3p0-config.xml"));
            log.warn("singleton instance construct " + SingletonFactory.class);

            ConfigMgr configMgr = ConfigMgr.getInstance();

            instance = new PoolC3p0Mysql();
            instance.setDriver(configMgr.get("jdbc.mysql.DriverClass", "com.mysql.jdbc.Driver"));
            instance.setUrl(configMgr.get("jdbc.mysql.JdbcUrl", "jdbc.mysql.JdbcUrl=jdbc:mysql://127.0.0.1:3306/walker?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true"));
            instance.setUser(configMgr.get("jdbc.mysql.User", "walker"));
            instance.setUser(configMgr.get("jdbc.mysql.Password", null));

//            jdbc.mysql.DriverClass=com.mysql.jdbc.Driver
//            jdbc.mysql.JdbcUrl=jdbc:mysql://39.106.111.11:8098/walker?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
//            jdbc.mysql.User=walker
//            jdbc.mysql.Password=12345

        }
    }


}
