package com.walker.core.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.walker.core.Context;
import com.walker.core.cache.ConfigMgr;
import com.walker.util.FileUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
@Data
public class PoolC3p0Sqlite extends Pool {
    private final static Logger log = LoggerFactory.getLogger(PoolC3p0Sqlite.class);
    String driver;
    String url;
    String user;
    String pwd;
    private ComboPooledDataSource ds;

    /**
     * 单例模式
     */
    public static PoolC3p0Sqlite getInstance() {
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

    @Override
    String getDsName() {
        return "sqlite";
    }

    private static class SingletonFactory {
        static PoolC3p0Sqlite instance;

        static {
            System.setProperty("com.mchange.v2.c3p0.cfg.xml", Context.getPathConf("c3p0-config.xml"));
            log.warn("singleton instance construct " + SingletonFactory.class);

            ConfigMgr configMgr = ConfigMgr.getInstance();
            instance = new PoolC3p0Sqlite();
            instance.setDriver(configMgr.get("jdbc.sqlite.DriverClass", "org.sqlite.JDBC"));
            instance.setUrl(configMgr.get("jdbc.sqlite.JdbcUrl", "jdbc:sqlite:/db/walker.sqlite.db"));
            instance.setUser(configMgr.get("jdbc.sqlite.User", ""));
            instance.setUser(configMgr.get("jdbc.sqlite.Password", ""));

            String file = instance.getUrl().split(":")[2];
            FileUtil.mkfile(file);

        }
    }


}
