package com.walker.listener;

import com.walker.service.Config;
import com.walker.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 生命周期
 *
 * spring容器启动后执行
 *
 */
@Component
public class OnPostConstruct {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    InitService initService;

    @PostConstruct
    public void init() throws Exception {
        log.info(Config.getPre() + "OnPostConstruct init " + String.valueOf(initService));


        initService.initOnStart();


    }
}