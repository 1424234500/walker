package com.walker.listener;

import com.walker.service.Config;
import com.walker.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 生命周期
 *
 * spring容器启动后执行
 *
 * 在web 项目中（spring mvc），系统会存在两个容器，一个是root application context ,另一个就是我们自己的 projectName-servlet context（作为root application context的子容器）。这种情况下，就会造成onApplicationEvent方法被执行两次。为了避免上面提到的问题，我们可以只在root application context初始化完成后调用逻辑代码，其他的容器的初始化完成，则不做任何处理。
 *
 * event.getApplicationContext().getParent() == null
 *
 */
@Component
public class OnApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    InitService initService;
    static int count = 0;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        count++;
//        if (event.getApplicationContext().getParent() == null)
        log.info(Config.getPre() + "OnApplicationListener onApplicationEvent " + count + " " + String.valueOf(initService));





    }
}