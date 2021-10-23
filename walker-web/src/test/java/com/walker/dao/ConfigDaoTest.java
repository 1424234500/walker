package com.walker.dao;

import com.walker.ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigDaoTest extends ApplicationTests {

    @Autowired
    ConfigDao configDao;

    @Test
    public void reload() {
        int i = 0;
        out(configDao);
        out(i++, "get", configDao.get("key", "default1"));
        out(i++, "set", configDao.set("key", "value", "about"));

        configDao.reload();

        out(i++, "set", configDao.set("key1", "value1", "about1"));
        out(i++, "get", configDao.get("key", "default2"));
        out(i++, "get", configDao.get("key", "default3"));
        out(i++, "set", configDao.set("key", "value", "about"));
        out(i++, "get", configDao.get("key", "default4"));
        out(i++, "get", configDao.get("key", "default5"));


    }

}