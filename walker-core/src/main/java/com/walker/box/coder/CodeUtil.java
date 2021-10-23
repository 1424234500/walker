package com.walker.box.coder;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class CodeUtil {

    public static String getList(List<String> list, int col, String defaultValue){
        String res = defaultValue;
        if(list != null && list.size() > col){
            String s = list.get(col);
            if(s != null && s.length() > 0){
                res = StringUtils.strip(s);
            }
        }
        return res;
    }
    public static String turnCode(String code) {
        return turnUnderlineToCamel(code);
    }
    public static String turnPackageToPath(String pack) {
        return  pack.replace(".", File.separator);
    }

    public static final char UNDERLINE = '_';

    public static String turnUpperFirst(String code){
        return (code.charAt(0) + "").toUpperCase() + code.substring(1);
    }
    public static String turnLowerFirst(String code){
        return (code.charAt(0) + "").toLowerCase() + code.substring(1);
    }

    //驼峰转下划线
    public static String turnCamelToUnderline(String param, Integer charType) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
            }
            if (charType == 2) {
                sb.append(Character.toUpperCase(c));  //统一都转大写
            } else {
                sb.append(Character.toLowerCase(c));  //统一都转小写
            }


        }
        return sb.toString();
    }

    //下划线转驼峰
    public static String turnUnderlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        Boolean flag = false; // "_" 后转大写标志,默认字符前面没有"_"
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                flag = true;
                continue;   //标志设置为true,跳过
            } else {
                if (flag == true) {
                    //表示当前字符前面是"_" ,当前字符转大写
                    sb.append(Character.toUpperCase(param.charAt(i)));
                    flag = false;  //重置标识
                } else {
                    sb.append(Character.toLowerCase(param.charAt(i)));
                }
            }
        }
        return sb.toString();
    }

    /**
     * sql数据结构转换java模型结构 oracle mysql兼容性
     */
    public static String turnSqlType2JavaType(String type) {
        String res = "";
        type = type.toUpperCase();
        if (type.startsWith("INT")
                || type.startsWith("BIGINT")
                || type.startsWith("ID")
                || type.startsWith("INTEGER")
                ) {
            res = "Integer";
        } else if (type.startsWith("LONG")
                ) {
            res = "Long";
        } else if (type.startsWith("NUMBER")
                || type.startsWith("FLOAT")
                ) {
            res = "Float";
        }  else if (type.startsWith("DOUBLE")
                ) {
            res = "Double";
        } else if (type.startsWith("TINYINT")
                || type.startsWith("SMALLINT")
                || type.startsWith("MEDIUMINT")
                ) {
            res = "Integer";
        }
        else if (type.startsWith("BIGINT")
                ) {
            res = "BigInteger";
        } else if (type.startsWith("DECIMAL")
                ) {
            res = "BigDecimal";
        }else if (type.startsWith("BIT")
                || type.startsWith("BOOLEAN")
                ) {
            res = "Boolean";
        }


        else if (type.startsWith("VARCHAR")
                || type.startsWith("TEXT")
                || type.startsWith("LONGTEXT")
                || type.startsWith("CHAR")
                || type.startsWith("ENUM")
                ) {
            res = "String";
        }  else if (type.startsWith("TIMESTAMP")
                || type.startsWith("TIME")
                || type.startsWith("DATE")
                ) {
            res = "Date";
        }else if (type.startsWith("BLOB")
                ) {
            res = "Byte[]";
        }
        return res;
    }


    public static String noQuoto(String queryEg){
        if(queryEg.length() > 0 && queryEg.endsWith("'") && !queryEg.startsWith("'")){
            queryEg = "'" + queryEg;
        }
        String queryEgNoQuoto = queryEg;
        if(queryEgNoQuoto.length() > 0 && queryEgNoQuoto.startsWith("'")){
            queryEgNoQuoto = queryEgNoQuoto.substring("'".length());
        }
        if(queryEgNoQuoto.length() > 0 && queryEgNoQuoto.endsWith("'")){
            queryEgNoQuoto = queryEgNoQuoto.substring(0, queryEgNoQuoto.length() - "'".length());
        }
        return queryEgNoQuoto;
    }

}