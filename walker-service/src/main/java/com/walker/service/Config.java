package com.walker.service;

import com.walker.util.Tools;

public class Config {

    public static final int MAX_VARCHAR_LEN = 1998;
    private static int count = 0;
    public static String PRE = "init--------";
    public static String getPre(){
        return "#" + Tools.fillStringBy(count++ + ".", " ", 4, 1) + PRE ;
    }


    public static final String TRUE = "1";
    public static final String FALSE = "0";


    public static String getUploadDir() {
        return "/home/walker/files";
    }
    public static String getDownloadDir() {
        return "/home";
    }
    public static String getTempImageDir(){
        return "/home/walker/files/tmp";
    }

    public static int getDbsize(){
        return 500;
    }
    public static int getDbVarcharLengthLong(){
        return 1990;
    }

    public static String makePrefix(String prefix, String ID) {
        if(ID != null && ID.length() > 0 && !ID.startsWith(prefix)){
            ID = prefix + ID;
        }
        return ID;
    }
    public static String cutString(int len, String str) {
        len--;
        if(str != null && str.length() > len ){
            int dl = (len - "...".length() ) / 2;
            str = str.substring(0, dl) + "..." + str.substring(str.length() - dl);
        }
        return str;
    }


    public static String getSystemUser() {
        return "system";
    }
    public static String getCateController() {
        return "controller";
    }
    public static String getCateTest() {
        return "test";
    }
    public static String getCateJob() {
        return "job";
    }


}
