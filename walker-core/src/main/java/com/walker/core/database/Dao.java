package com.walker.core.database;

import com.walker.util.Watch;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据库常用操作工具 选择一种连接池实现 每种连接池对应多种数据源 多种数据库
 * <p>
 * jdbc 一个实例 绑定 连接池 和 数据源 但 底层 是同一个连接池 且 每个连接池每个 数据源是 唯一
 */
public class Dao extends BaseDaoAdapter {

    private final Pool pool;

    public Dao(Pool pool) {
        this.pool = pool;
    }


    public Dao() {
        this(PoolC3p0Mysql.getInstance());

    }

    @Override
    public String getDs() {
        return pool.getDsName();
    }

    /**
     * 获取链接
     */
    private Connection getConnection() throws SQLException {
        return pool.getConn();
    }

    /**
     * 关闭连接
     */
    private void close(Connection conn, PreparedStatement pst, ResultSet rs) {
        pool.close(conn, pst, rs);
    }

    @Override
    public List<Map<String, Object>> find(String sql, Object... objects) {
        sql = SqlUtil.filter(sql);
        Watch w = new Watch(SqlUtil.makeSql(sql, objects));
        List<Map<String, Object>> res = null;
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                pst.setObject(i + 1, objects[i]);
            }
            rs = pst.executeQuery();
            res = SqlUtil.toListMap(rs);
            w.res(res, log);
        } catch (Exception e) {
            w.exceptionWithThrow(e, log);
        } finally {
            close(conn, pst, rs);
        }
        return res;

    }

    @Override
    public Integer executeSql(String sql, Object... objects) {
        sql = SqlUtil.filter(sql);
        Watch w = new Watch(SqlUtil.makeSql(sql, objects));
        int res = 0;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                pst.setObject(i + 1, objects[i]);
            }
            res = pst.executeUpdate();
            w.res(res, log);
        } catch (Exception e) {
            w.exceptionWithThrow(e, log);
        } finally {
            close(conn, pst, null);
        }
        return res;
    }

    @Override
    public Integer[] executeSqlBatch(String sql, List<List<Object>> objs) {
        sql = SqlUtil.filter(sql);
        Watch w = new Watch(sql).put("size", objs.size()).put("eg", objs.get(0));
        Integer[] res = {};
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);
            for (List<Object> objects : objs) {
                for (int i = 0; i < objects.size(); i++) {
                    pst.setObject(i + 1, objects.get(i));
                }
                pst.addBatch();
            }
            int[] resint = pst.executeBatch();
            res = new Integer[resint.length];
            for (int i = 0; i < resint.length; i++) {
                res[i] = resint[i];
            }
            w.res(Arrays.toString(res), log);
        } catch (Exception e) {
            w.exceptionWithThrow(e, log);
        } finally {
            close(conn, pst, null);
        }
        return res;
    }

    /**
     * 调用存储过程的语句，call后面的就是存储过程名和需要传入的参数
     *
     * @param proc    "{call countBySal(?,?)}"
     * @param objects
     */
    @Override
    public Integer executeProc(String proc, Object... objects) {
        Watch w = new Watch(proc);
        int res = 0;

        Connection conn = null;
        CallableStatement cst = null;
        try {
            conn = this.getConnection();

            cst = conn.prepareCall(proc);
            for (int i = 0; i < objects.length; i++) {
                if (i == objects.length - 1) {
                    cst.registerOutParameter(objects.length + 1, Types.INTEGER);// 注册out参数的类型
                } else {
                    cst.setObject(i + 1, objects[i]);
                }
            }
            cst.execute();
            res = cst.getInt(objects.length);
            w.res(res, log);
        } catch (Exception e) {
            w.exceptionWithThrow(e, log);
        } finally {
            close(conn, cst, null);
        }
        return res;
    }

}
