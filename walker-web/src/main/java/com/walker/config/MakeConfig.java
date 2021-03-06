package com.walker.config;


import com.walker.service.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 自定义配置文件
 *
 用@Configuration注解该类，等价于XML中配置beans；用@Bean标注方法等价于XML中配置bean。
 */
@Configuration
@PropertySource({"classpath:make.properties"})
//@Component("makeConfig")
public class MakeConfig {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${url.area.meituan:https://www.meituan.com/ptapi/getprovincecityinfo/}")
    public String urlAreaMeituan;

    @Value("${url.area.gov:http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html}")
    public String urlAreaGov;

    @Value("${test}")
    public String test;

    /**
     * 全局变量读取设置
     */
    public static String TEST;
    @Value("${test}")
    public void setExamplePath(String test) {
        log.info(Config.getPre() + "MakeConfig read static properties " + test);
        MakeConfig.TEST = test;
    }


    public String toString(){
        String res = "test:" + test;
        log.info(res);
        return res;
    }


}
