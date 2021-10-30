package com.walker.core.database;

import com.walker.core.exception.ErrorException;
import com.walker.core.exception.ExceptionUtil;
import com.walker.util.FileUtil;
import com.walker.util.Model;
import com.walker.util.Tools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 数据库sql语句帮助
 *
 * @author Walker
 * 2017年9月18日17:33:25
 */
public class SqlUtil {
    private static final Logger log = LoggerFactory.getLogger(SqlUtil.class);


    /**
     * 字符串模糊查询 添加符号  "%" + value + "%"
     */
    public static String like(String value) {
        return "%" + value + "%";
    }

    /**
     * 字符串 单引号包围"'" + value + "'"
     */
    public static String include(String value) {
        return "'" + value + "'";
    }

    /**
     * 年月日 时分秒 格式'yyyy-mm-dd hh24:mi:ss'
     */
    public static String getTimeFormatL() {
        return "'yyyy-mm-dd hh24:mi:ss'";
    }

    /**
     * 年月日 格式'yyyy-mm-dd'
     */
    public static String getTimeFormatS() {
        return "'yyyy-mm-dd'";
    }

    /**
     * 转换 to_date 年月日 时分秒 to_date('time','yyyy-mm-dd hh24:mi:ss')
     */
    public static String to_dateL(String time) {
        return " to_date(" + include(time) + ", " + getTimeFormatL() + ") ";
    }

    /**
     * 转换 to_date 年月日 时分秒 to_date(?,'yyyy-mm-dd hh24:mi:ss')
     */
    public static String to_dateL() {
        return " to_date(?, " + getTimeFormatL() + ") ";
    }

    /**
     * 转换 to_date 年月日   to_date('time','yyyy-mm-dd')
     */
    public static String to_dateS(String time) {
        return " to_date(" + include(time) + ", " + getTimeFormatS() + ") ";
    }

    /**
     * 转换 to_date 年月日  to_date(?,'yyyy-mm-dd')
     */
    public static String to_dateS() {
        return " to_date(?, " + getTimeFormatS() + ") ";
    }

    /**
     * 转换 to_char 年月日 时分秒
     */
    public static String to_charL(String time) {
        return " to_char(" + include(time) + ", " + getTimeFormatL() + ") ";
    }

    /**
     * 转换 to_char 年月日 时分秒
     */
    public static String to_charL() {
        return " to_char(?, " + getTimeFormatL() + ") ";
    }

    /**
     * 转换 to_char 年月日
     */
    public static String to_charS(String time) {
        return " to_char(" + include(time) + ", " + getTimeFormatS() + ") ";
    }

    /**
     * 转换 to_char 年月日
     */
    public static String to_charS() {
        return " to_char(?, " + getTimeFormatS() + ") ";
    }

    /**
     * 位数补齐 用c补齐到tolen位
     */
    public static String file(String value, int tolen, char c) {
        return " lpad(" + value + ", " + tolen + ", '" + c + "') ";
    }

    /**
     * 根据map制作建表语句
     */
    public static String makeSqlCreate(String tableName, Map<String, Object> map) {
        Set<String> keys = map.keySet();
        List<Integer> lengths = new ArrayList<>();
        for (String key : keys) {
            lengths.add(String.valueOf(map.get(key)).length() + 10);
        }
        return makeSqlCreate(tableName, keys, lengths, 20);
    }

    /**
     * 根据map制作建表语句
     */
    public static String makeSqlCreate(String tableName, Set<String> keys, List<Integer> lengths, Integer defaultLength) {
        if (tableName == null || tableName.length() == 0) {
            throw new RuntimeException("tableName can't null ");
        }
        if (keys == null || keys.size() == 0) {
            throw new RuntimeException("keys can't null ");
        }
        if (lengths == null) {
            lengths = new ArrayList<>();
        }
        for (int i = lengths.size(); i < keys.size(); i++) {
            lengths.add(defaultLength);
        }
        Object[] keyss = keys.toArray();
        StringBuilder sb = new StringBuilder("create table ").append(tableName).append("(");
        for (int i = 0; i < keyss.length; i++) {
            String key = String.valueOf(keyss[i])
                    .replace("'", "")
                    .replace("\"", "");
            sb.append(key).append(" varchar(").append(lengths.get(i)).append("), ");
        }
        sb.setLength(sb.length() - ", ".length());

        sb.append(" )");
        return sb.toString();
    }

    /**
     * 根据map制作建表语句
     */
    public static String makeSqlInsert(String tableName, Set<String> keys) {
        if (tableName == null || tableName.length() == 0) {
            throw new RuntimeException("tableName can't null ");
        }
        if (keys == null || keys.size() == 0) {
            throw new RuntimeException("keys can't null ");
        }
        Object[] keyss = keys.toArray();
        StringBuilder sb = new StringBuilder("insert into ").append(tableName).append("(");
        for (int i = 0; i < keyss.length; i++) {
            String key = String.valueOf(keyss[i])
                    .replace("'", "")
                    .replace("\"", "");
            sb.append(key).append(", ");
        }
        sb.setLength(sb.length() - ", ".length());

        sb.append(" )")
                .append(" values (")
                .append(makePosition("?", keyss.length))
                .append(")");
        return sb.toString();
    }

    /**
     * 为map的每个键 制作占位符
     * ?, ?, ?
     */
    public static String makeMapPosis(Map<?, ?> map) {
        return makePosition("?", map.size());
    }

    /**
     * 为map的每个键制作别名
     * id, value, name
     */
    public static String makeMapKeys(Map<?, ?> map) {
        String res = "  ";
        for (Object key : map.keySet()) {
            res = res + key + ", ";
        }
        res = res.substring(0, res.length() - 2);
        return res;
    }

    /**
     * id=?, value=?, name=?
     */
    public static String makeMapKeyPosis(Map<?, ?> map) {
        String res = "";
        for (Object key : map.keySet()) {
            res = res + key + "=?, ";
        }
        res = res.substring(0, res.length() - 2);
        return res;
    }

    /**
     * id1, value2, name3
     */
    public static String makeMapValues(Map<?, ?> map) {
        String res = "  ";
        for (Object key : map.keySet()) {
            res = res + (map.get(key)).toString() + ", ";
        }
        res = res.substring(0, res.length() - 2);
        return res;
    }

    public static int getMapSize(Map<?, ?> map) {
        return map.size();
    }

    /**
     * 设置n个问号 ？ 占位符
     */
    public static String makePosition(String posi, int num) {
        String res = "";
        for (int i = 0; i < num - 1; i++) {
            res = res + posi + ", ";
        }
        res = res + posi;
        return res;
    }

    /**
     * sql占位符替换拼接参数 生成sql  仅作参考sql语句使用 易被sql注入
     */
    public static String makeSql(String sql, Object... objects) {
        if (sql.length() <= 0) return sql;
        if (sql.charAt(sql.length() - 1) != ' ') {    //最后面加空格 因为split最后的符号不分
            sql = sql + " ";
        }

        int needLen = sql.split("\\?").length - 1;
        int realLen = 0;
        if (objects != null && objects.length > 0) {
            realLen = objects.length;
        }
        if (needLen != realLen) {
            sql = "Error! "
                    + "\nsql: " + sql
                    + "\nobjects: " + Tools.objects2string(objects)
                    + "\n占位符个数[" + needLen + "]与实际参数个数[" + realLen + "]不同";
        } else if (needLen > 0) {//sf s where id=? and name=? order by ? 。
            int t = -1;
            for (int i = 0; i < objects.length; i++) {
                t = sql.indexOf("?");
                sql = sql.substring(0, t) + (objects[i] == null ? "null" : "'" + objects[i] + "'") + sql.substring(t + 1);
            }
        }
        return sql;
    }

    /**
     * sql特殊字符过滤  字符兼容
     *
     * @param sql
     */
    public static String filter(String sql) {
        ExceptionUtil.blankThrow(Model.build("sql", sql));
        sql = StringUtils.strip(sql);
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        return sql;
    }

    /**
     * 根据不同数据源构造不同分页sql
     *
     * @param sql
     * @param page
     * @param rows
     */
    public static String makeSqlPage(String dsName, String sql, int page, int rows) {
        dsName = String.valueOf(dsName);
        int start = ((page - 1) * rows);
        if (dsName.equals("mysql") || dsName.equals("sqlite")) {
            sql = "select * from ( " + sql + " ) t limit " + start + "," + rows;    //2,5 -> 56789 -> 5,5
        } else if (dsName.equals("oracle")) {                                        //2,5 -> 67890 -> 5,10
            int stop = page * rows + 1;
            sql = " select * from ( select t.*,rownum rowno from ( "
                    + sql +
                    " ) t where rownum <=  " + stop + " ) where rowno > " + start;
        } else {
            throw new ErrorException("no implements  " + dsName);
        }
        return sql;
    }

    public static String makeSqlPageRand(String dsName, String sql, int size) {
        dsName = String.valueOf(dsName);
        if (dsName.equals("mysql") || dsName.equals("sqlite")) {
            sql = "select * from ( " + sql + " ) t order by RAND() limit " + size;    //2,5 -> 56789 -> 5,5
        } else if (dsName.equals("oracle")) {                                        //2,5 -> 67890 -> 5,10
            sql = " select * from ( select t.*,rownum rowno from ( "
                    + sql +
                    " ) t where rownum <=  " + size + " order by DBMS_RANDOM.RANDOM() )";
        } else {
            throw new ErrorException("no implements  " + dsName);
        }
        return sql;
    }

    /**
     * 结果集转换 列 值
     *
     * @param rs
     * @return [ { id:1,name:n1}, {id:2,name:n2}  ]
     * @throws SQLException
     */
    public static List<Map<String, Object>> toListMap(ResultSet rs) throws SQLException {
        List<Map<String, Object>> res = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
        while (rs.next()) {
            Map<String, Object> map = new LinkedHashMap<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                map.put(md.getColumnName(i), rs.getObject(i));
            }
            res.add(map);
        }
        return res;
    }

    /**
     * 结果集转换 值
     *
     * @param rs
     * @throws SQLException
     * @return [ [1, n1], [2,n2] ]
     */
    public static List<List<String>> toValues(ResultSet rs) throws SQLException {
        List<List<String>> res = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
        while (rs.next()) {
            ArrayList<String> list = new ArrayList<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                list.add(String.valueOf(rs.getObject(i)));
            }
            res.add(list);
        }
        return res;
    }

    /**
     * 结果集转换 值
     *
     * @param rs
     * @throws SQLException
     * @return [ [1, n1], [2,n2] ]
     */
    public static List<List<String>> toValues(List<Map<String, Object>> rs) {
        List<List<String>> res = new ArrayList<>();
        for (Map<String, Object> map : rs) {
            ArrayList<String> list = new ArrayList<>();
            for (String key : map.keySet()) {
                list.add(String.valueOf(map.get(key)));
            }
            res.add(list);
        }
        return res;
    }

    /**
     * 结果集转换 列
     *
     * @param rs
     * @throws SQLException
     * @return [ id, name ]
     */
    public static List<String> toKeys(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        List<String> res = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            res.add(md.getColumnName(i + 1));
        }
        return res;
    }


    /**
     * 结果集转换 列
     *
     * @param rs
     * @throws SQLException
     * @return [ id, name ]
     */
    public static List<String> toKeys(List<Map<String, Object>> rs) {
        List<String> res = new ArrayList<>();

        if (rs.size() > 0) {
            Map<String, Object> map = rs.get(0);
            res.addAll(map.keySet());
        }
        return res;
    }

    /**
     * 执行sql文件
     *
     * @param baseDao
     * @param path
     */
    public static void executeSqlFile(BaseDao baseDao, String path) {
        FileUtil.readByLines(path, new FileUtil.Fun<String>() {
            @Override
            public void make(String obj) {
                try {
                    obj = StringUtils.strip(obj);
                    if (!obj.startsWith("--") && obj.length() > 0) {
                        log.info(obj);
                        baseDao.executeSql(obj);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, null);
    }

    /**
     * 计数sql
     *
     * @param sql
     */
    public static String makeSqlCount(String sql) {
        return "select count(*) from (" + sql + ") t ";
    }


    /**
     * 排序sql
     *
     * @param sql
     * @param order
     */
    public static String makeSqlOrder(String sql, String order) {
        if (order != null && order.length() > 0) {
            return "select * from (" + sql + ") t order by " + order;
        }
        return sql;
    }

    /**
     * 分表 表名生成
     * 分表依赖键值 至少一个
     * 分表数量
     */
    public static int makeTableCount(int count, String... keys) {
        String key = makeTableKey(keys);
        return Math.abs(key.hashCode()) % count;
    }

    /**
     * 生成分表键 无序
     */
    public static String makeTableKey(String... keys) {
        Arrays.sort(keys);
        StringBuilder key = new StringBuilder();
        for (String str : keys) {
            key.append(str).append(":");
        }
        return key.toString();
    }

    /**
     * 检测是否查询类型sql
     *
     * @param sql
     */
    public static boolean isSelect(String sql) {
        boolean res = false;

        sql = StringUtils.strip(sql).toUpperCase();
        int i = sql.indexOf("SELECT");
        res = i > sql.indexOf("UPDATE") && i > sql.indexOf("DELETE");

        return res;
    }
}