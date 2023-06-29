package com.walker.core.annotation;

import com.walker.core.util.Tools;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解案例
 * 目标注解捕获 处理器
 * <p>
 * 处理数据库相关注解
 * 不能继承 上转 所以只能特定转换 处理
 */

public class DBTableTracker implements OnAnnotation {


    @Override
    public Status make(Annotation annotation, ElementType type, Object object, Class<?> cls) {
        Tools.out(this, annotation, type, object);
        if (type.equals(ElementType.TYPE))
            track(annotation, (Class<?>) object);
        else if (type.equals(ElementType.FIELD))
            Tools.out("db field no make");
        return null;
    }

    public void track(Annotation anno, Class<?> cl) {
//		DBTable dbTable = cl.getAnnotation(DBTable.class);
        DBTable dbTable = (DBTable) anno;
        Tools.out("dbtable make");

        String tname = dbTable.name();
        if (tname.length() < 1) { //默认类名
            tname = cl.getSimpleName().toUpperCase();
        }
        List<String> colDefs = new ArrayList<>();
        for (Field field : cl.getDeclaredFields()) {
            String colName = null;
            Annotation[] anns = field.getDeclaredAnnotations();
            if (anns.length < 1) {
                continue;
            }

            //不能继承 上转 所以只能特定转换 处理
            if (anns[0] instanceof DBSQLInteger) {
                DBSQLInteger sint = (DBSQLInteger) anns[0];
                colName = sint.name();
                if (colName.length() < 1) {
                    colName = field.getName().toUpperCase(); //默认变量名
                }
                colDefs.add(colName + " INT" + getConstraints(sint.DBConstraints()));
            }
            if (anns[0] instanceof DBSQLString) {
                DBSQLString sint = (DBSQLString) anns[0];
                colName = sint.name();
                if (colName.length() < 1) {
                    colName = field.getName().toUpperCase(); //默认变量名
                }
                colDefs.add(colName + " VARCHAR(" + sint.value() + ") " + getConstraints(sint.DBConstraints()));
            }
        }
        StringBuilder sb = new StringBuilder("\ncreate table " + tname + "( ");
        for (String item : colDefs) {
            sb.append("\n\t" + item + ",");
        }
        String sql = sb.substring(0, sb.length() - 1) + " \n" + " );";
        Tools.out(sql);


    }

    private String getConstraints(DBConstraints con) {
        String res = "";
        if (!con.allowNull()) {
            res += " not null";
        }
        if (con.primaryKey()) {
            res += " primary key";
        }
        if (con.unique()) {
            res += " unique";
        }
        return res;
    }


}