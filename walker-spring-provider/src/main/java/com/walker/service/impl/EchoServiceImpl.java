package com.walker.service.impl;

import com.walker.service.EchoService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;


@org.springframework.stereotype.Service
@com.alibaba.dubbo.config.annotation.Service
@Transactional
//@Scope("prototype")
public class EchoServiceImpl implements EchoService, Serializable {
    private static final long serialVersionUID = 8304941820771045214L;

    /**
     * 回响
     *
     * @param hello
     */
    @Override
    public String echo(String hello) {
        return "echo:" + hello;
    }
}