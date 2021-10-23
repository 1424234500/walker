package com.walker.design.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv) {

        UserMapper joe = new UserMapperImpl();
        joe.setName("joe");
        log.info(joe.toString());
        log.info(joe.hello());

        UserMapper joeOwnerProxy = UserMapperFactory.getOwnerUserMapper(joe);
        try {
            log.info(joeOwnerProxy.hello());
            joeOwnerProxy.setName("joeProxy");
            joeOwnerProxy.addScore();
        } catch (Throwable e) {
            log.error(joeOwnerProxy + " " + e.getMessage());
        }
        UserMapper noOwnerProxy = UserMapperFactory.getNoOwnerUserMapper(joe);
        try {
            log.info(noOwnerProxy.hello());
            noOwnerProxy.addScore();
            noOwnerProxy.setName("nojoeProxy");
        } catch (Throwable e) {
            log.error(noOwnerProxy + " " + e.getMessage());
        }


    }


}



