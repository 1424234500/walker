package com.walker.service;

import com.walker.mode.sys.PushBindModel;
import com.walker.mode.sys.PushModel;

import java.util.List;

/**
 * 推送服务抽象 代理 负责绑定
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
public interface PushAgentService{



    /**
     * 推送给目标用户
     */
    Integer push(PushModel pushModel);

    /**
     * 绑定用户id和推送id和推送类别
     * @param pushBindModel
     */
    List<PushBindModel> bind(PushBindModel pushBindModel);

    /**
     * 绑定用户id和推送id和推送类别
     * @param userId
     */
    List<PushBindModel> findBind(String userId);

    /**
     * 取消绑定用户id和推送id和推送类别
     * 0则取消所有
     * @param pushBindModels
     */
    List<PushBindModel> unbind(List<PushBindModel> pushBindModels);

    /**
     * 取消绑定用户id和推送id和推送类别
     * @param userId
     */
    List<PushBindModel> unbind(String userId);





}