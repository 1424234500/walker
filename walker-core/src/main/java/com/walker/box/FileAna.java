package com.walker.box;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件分析工具
 */
public class FileAna {

    static List<Ana> anaList = new ArrayList<>();
    static {
        anaList.add(new Ana() {
            @Override
            public <T> T ana(String line) {

                if(line.indexOf(key) >= 0){


                }



                return null;
            }
        }.setName("测试").setKey("hello"));

    }




}

abstract class Ana{
    String key = "";
    String name = "";

    public String getKey() {
        return key;
    }

    public Ana setKey(String key) {
        this.key = key;
        return this;
    }

    public String getName() {
        return name;
    }

    public Ana setName(String name) {
        this.name = name;
        return this;
    }


    public abstract <T> T ana(String line);

    public <T> T anaEg(String line){
        assert line != null;
        assert line.length() > 0;

        if(line.indexOf(key) > 0){
            return (T)line;
        }
        return null;
    }


}







