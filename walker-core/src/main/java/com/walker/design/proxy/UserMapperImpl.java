package com.walker.design.proxy;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 代理模式
 * <p>
 * 用户自己能更改设置，但不能给自己评论好评的问题
 */
public class UserMapperImpl implements UserMapper {
    String name;
    AtomicInteger score;

    public UserMapperImpl() {
        name = "nobody";
        score = new AtomicInteger(0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getScore() {
        return this.score.get();
    }

    @Override
    public void addScore() {
        this.score.addAndGet(1);
    }

    @Override
    public String hello() {
        return "world";
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
