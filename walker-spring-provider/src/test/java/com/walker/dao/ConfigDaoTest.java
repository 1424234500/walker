package com.walker.dao;

import com.walker.ApplicationProviderTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigDaoTest extends ApplicationProviderTests {

    @Autowired
    ConfigDao configDao;

    @Test
    public void get() {
        for (String str : "com.walker.service.impl.doBaseData,com.walker.service.impl.doAction".split(",")){
            String res = configDao.get(str, "");
            out(str, res);
            Assert.assertTrue(res.length() > 0);
        }
    }
}