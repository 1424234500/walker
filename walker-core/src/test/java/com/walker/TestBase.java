package com.walker;

import com.walker.core.cache.ConfigMgr;
import com.walker.core.util.Excel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 测试基类 控制配置 日志 默认测试参数常量
 */
public abstract class TestBase {
    public static Logger log = LoggerFactory.getLogger(Excel.class);

    public static Integer size = 3;
    public static Integer sizeMed = 10;
    public static Integer sizeMax = 100;
    public static Integer sizeMMM = 10000;
    public static String fileNameTest = "test_file";

    public static LinkedHashMap<String, Object> map;
    public static List<String> listString = Arrays.asList("item0", "item1", "item2");
    public static List<List<String>> listListString = Arrays.asList(
            Arrays.asList("item0", "item1", "item2"),
            Arrays.asList("item0", "item1", "item2"),
            Arrays.asList("item0", "item1", "item2")
    );

    static{

//        初始化缓存配置坐标
        ConfigMgr.getInstance();

        map = new LinkedHashMap<>();
        for(int i = 0; i < size; i++){
            map.put("key" + i, "value" + i);
        }



    }
    public TestBase(){
        log.info("init base");
        initData();
    }


    public abstract void initData();

}
