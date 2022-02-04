package com.walker.controller;


import com.walker.Response;
import com.walker.socket.model.Msg;
import com.walker.socket.model.UserSocket;
import com.walker.service.MessageService;
import com.walker.mode.Bean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/*
测试 jap jpaService

 */
@Api(value = "测试消息服务Sharding实现 ")
@Controller
@RequestMapping("/messageSharding")
public class MessageShardingController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("messageServiceSharding")
    private MessageService messageService;

    /**
     * 消息每张分表数量
     */
    @ApiOperation(value = "消息每张分表数量", notes = "")
    @ResponseBody
    @RequestMapping(value = "/sizeMsg.do", method = RequestMethod.POST, produces = "application/json")
    public Response sizeMsg() {
        return Response.makeTrue("sizeMsg", messageService.sizeMsg());
    }

    /**
     * 消息关联用户每张分表数量
     */
    @ApiOperation(value = "消息关联用户每张分表数量", notes = "")
    @ResponseBody
    @RequestMapping(value = "/sizeMsgUser.do", method = RequestMethod.POST, produces = "application/json")
    public Response sizeMsgUser() {
        return Response.makeTrue("sizeMsgUser", messageService.sizeMsgUser());
    }

    /**
     * 存储消息 先存redis 再存mysql 分表
     * 先存消息实体 再存用户关联
     */
    @ApiOperation(value = "存储消息 先存redis 再存mysql 分表先存消息实体 再存用户关联", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST, produces = "application/json")
    public Response save(
            @RequestParam(value = "toId", required = true, defaultValue = "001,002,003") String toId,
            @RequestParam(value = "data", required = true, defaultValue = "data") String data
    ) {

        List<String> toIds = Arrays.asList(toId.split(","));
        Msg msg = new Msg();
        msg.setPlugin("TEST_" + getClass().getSimpleName());
        msg.setToUserId(toIds);
        msg.setData(new Bean().set("data", data));
        msg.setFromUser(new UserSocket().setId(toIds.get(0)).setName("name"));
        msg.setTimeServerDo(System.currentTimeMillis());

        Long score = messageService.save(msg.getToUserId(), msg);
        return Response.makeTrue("save " + score, msg);
    }

    /**
     * 查消息实体
     */
    @ApiOperation(value = "查消息实体", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findMsg.do", method = RequestMethod.POST, produces = "application/json")
    public Response findMsg(
            @RequestParam(value = "msgId", required = true, defaultValue = "m_111") String msgId
    ) {
        return Response.makeTrue("findMsg", messageService.findMsg(msgId));
    }

    /**
     * 查消息实体	用MERGE合并主表模式
     *
     * @param msgId
     */
    @ApiOperation(value = "查消息实体\t用MERGE合并主表模式", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findMsgByMerge.do", method = RequestMethod.POST, produces = "application/json")
    public Response findMsgByMerge(
            @RequestParam(value = "msgId", required = true, defaultValue = "m_111") String msgId
    ) {
        return Response.makeTrue("findMsgByMerge", messageService.findMsgByMerge(msgId));
    }

    /**
     * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表
     *
     * @param userId 001
     * @param before "yyyy-MM-dd HH:mm:ss:SSS"
     * @param count  3
     */
    @ApiOperation(value = "查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findBefore.do", method = RequestMethod.POST, produces = "application/json")
    public Response findBefore(
            @RequestParam(value = "userId", required = true, defaultValue = "001") String userId
            , @RequestParam(value = "toId", required = true, defaultValue = "002") String toId
            , @RequestParam(value = "before", required = true, defaultValue = "2019-01-02 22:21:00:000") String before
            , @RequestParam(value = "count", required = true, defaultValue = "3") int count
    ) {

        return Response.makeTrue("findBefore", messageService.findBefore(userId, toId, before, count));
    }

    /**
     * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表	用MERGE合并主表模式 关联查询
     *
     * @param userId
     * @param toId
     * @param before
     * @param count
     */
    @ApiOperation(value = "查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findBeforeByMerge.do", method = RequestMethod.POST, produces = "application/json")
    public Response findBeforeByMerge(
            @RequestParam(value = "userId", required = true, defaultValue = "001") String userId
            , @RequestParam(value = "toId", required = true, defaultValue = "002") String toId
            , @RequestParam(value = "before", required = true, defaultValue = "2019-01-02 22:21:00:000") String before
            , @RequestParam(value = "count", required = true, defaultValue = "3") int count
    ) {

        return Response.makeTrue("findBeforeByMerge", messageService.findBeforeByMerge(userId, toId, before, count));
    }

    /**
     * 查询时间节点之后的数据 离线后搜到的消息
     *
     * @param userId
     * @param after
     * @param count
     */
    @ApiOperation(value = "查询时间节点之后的数据 离线后搜到的消息", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findAfter.do", method = RequestMethod.POST, produces = "application/json")
    public Response findAfter(
            @RequestParam(value = "userId", required = true, defaultValue = "001") String userId
            , @RequestParam(value = "after", required = true, defaultValue = "2019-01-02 22:21:00:000") String after
            , @RequestParam(value = "count", required = true, defaultValue = "3") int count
    ) {
        return Response.makeTrue("findAfter", messageService.findAfter(userId, after, count));
    }
}
