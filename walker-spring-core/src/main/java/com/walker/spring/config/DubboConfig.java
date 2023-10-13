package com.walker.spring.config;


import com.walker.service.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 自定义配置文件
 * <p>
 * 用@Configuration注解该类，等价于XML中配置beans；用@Bean标注方法等价于XML中配置bean。
 */
@Configuration
@ImportResource({"classpath:dubbo-service-config.xml"})
//@PropertySource("classpath:dubbo.properties")
public class DubboConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public DubboConfig() {
        log.info(Config.getPre() + " DubboConfig ");
    }


}
