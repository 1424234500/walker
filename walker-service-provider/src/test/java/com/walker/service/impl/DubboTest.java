package com.walker.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.walker.ApplicationProviderTests;
import com.walker.service.MessageService;
import org.junit.Test;

public class DubboTest extends ApplicationProviderTests {

    @Reference(version = "1.0.0")
    public MessageService messageService;
    @Test
    public void testDubboConsumer(){
//        Tools.out("dubbo-comsumer-sizeMsg", messageService.sizeMsg());
        out("no implemnets");
    }


}
