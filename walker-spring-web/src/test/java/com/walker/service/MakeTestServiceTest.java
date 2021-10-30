package com.walker.service;

import com.walker.ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class MakeTestServiceTest extends ApplicationTests {

    @Autowired
    MakeTestService makeTestService;
    @Test
    public void makeUrlTestRandom() {
        makeTestService.makeUrlTestRandom(20000, 10, 1);
    }
}