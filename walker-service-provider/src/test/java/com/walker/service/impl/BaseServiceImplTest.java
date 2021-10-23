package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.service.BaseService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseServiceImplTest extends ApplicationProviderTests {
    @Autowired
    BaseService baseService;
//sharding 拦截了 INFORMATION_SCHEMA表?
//    @TestModel
//    public void getColumnsByTableName() {
//        out(baseService.getColumnsByTableName("TEACHER"));
//    }
//    @TestModel
//    public void getColumnsMapByTableName() {
//        out(baseService.getColumnsMapByTableName("USER"));
//    }
    @Test
    public void test() {
        out(baseService.find("select 'a' a, 'b' b from dual "));
    }
}