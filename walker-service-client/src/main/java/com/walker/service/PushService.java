package com.walker.service;

import com.walker.mode.PushModel;

import java.util.Set;

/**
 * 推送服务抽象
 *
 * 我 要推送 给
 *
 * 某客户端id
 * title
 * content
 * type 提醒 or 透传
 * ext  其他参数 携带
 *
 * 返回
 *
 */
public interface PushService {

    /**
     * 推送给目标用户
     */
    Integer push(PushModel pushBindModel, Set<String> pushIds);

    String getType();


}