package com.walker.mode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * socket 传递 消息结构
 * <p>
 * who 发来了一条消息  session:socket:key
 * {type:login,data:{user:walker,pwd:1234,device:xxxxx} }
 * {type:login,status:0,data:{user:walker,pwd:1234,device:xxxxx} }
 * {type:login,status:1,info:重复id,data:{user:walker,pwd:1234,device:xxxxx} }
 * <p>
 * {type:message,data:{user:walker,pwd:1234} }
 * <p>
 * 客户端写入数据
 * type:login	登录请求
 * data:{}		登录请求的参数
 * <p>
 * <p>
 * tcp/ip 七层协议结构分层
 */
@Data
public class Msg implements Cloneable, Serializable {
    public static final long serialVersionUID = 1L;
    final public static String SPLIT = ",";

// * 	系统监控层
// * 	各个时间节点 堆积深度 异常信息

    //记录关键时间节点 统计计算
    //time_client - 网络传输耗时 - time_receive - 队列等待耗时 - time_do - 业务处理耗时 - time_send
    long timeClientSend;
    long timeServerRecv;
    long timeServerDo;
    long timeServerSend;
    Long queueWaitDepth; //pipe 队列等待深度
    String sysInfo = "";

    String id = "";  //db
    String idFromClient = "";  //客户端生成id发送
    // * 	业务对象层
// * 	谁发的(user) 谁收 单发 群发(分发)
    UserSocket fromUser; //来源自动装配
//    List<UserSocket> toUser; //目标自动装配

    // *  业务 id层                              登录绑定 userId-socketKey
// *  谁发的(userId) 谁收 单发 群发(分发)
//    String fromUserId; //来源自动装配
    List<String> toUserId = new ArrayList<>(); //目标设置


    // * 	socket key层                           连接绑定 socketKey-socket
// * 	谁发的(socketKey) 谁收 单发 群发(分发)
    String fromSocketKey;  //来源自动装配
//    List<String> toSocketKey;   //目标映射

    // * 	业务入参
    String plugin = "";  //业务请求 分类转发处理
    Object data;    //业务参数 泛型转换 plugin 前后转换重大问题!!!!!!!

    // * 	业务出参
    int status = 0;  //接口响应标记状态
    Object res; //接口响应结果


    public static Msg parse(String jsonstr) {
        return JSON.parseObject(jsonstr, new TypeReference<Msg>() {
        });
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

}
