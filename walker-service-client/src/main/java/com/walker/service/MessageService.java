package com.walker.service;

import com.walker.mode.Msg;

import java.util.List;


public interface MessageService  {
    /**
     * 消息每张分表数量
     */
    List<Integer> sizeMsg();
    /**
     * 消息关联用户每张分表数量
     */
    List<Integer> sizeMsgUser();


    /**
     * 存储消息 先存redis 再存mysql 分表
     * 先存消息实体 再存用户关联
     * @param toIds
     * @param msg
     */
    Long save(List<String> toIds, Msg msg);

    /**
     * 查消息实体
     * @param msgId
     */
    Msg findMsg(String msgId);
    /**
     * 查消息实体	用MERGE合并主表模式
     * @param msgId
     */
    Msg findMsgByMerge(String msgId);
    /**
     * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表
     * @param userId    001
     * @param before    "yyyy-MM-dd HH:mm:ss:SSS"
     * @param count 3
     */
    List<Msg> findBefore(String userId, String toId, String before, int count);
    /**
     * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表	用MERGE合并主表模式 关联查询
     * @param userId
     * @param before
     * @param count
     */
    List<Msg> findBeforeByMerge(String userId, String toId, String before, int count);

    /**
     * 查询时间节点之后的数据 离线后搜到的消息
     * @param userId
     * @param after
     * @param count
     */
    List<Msg> findAfter(String userId, String after, int count);

}