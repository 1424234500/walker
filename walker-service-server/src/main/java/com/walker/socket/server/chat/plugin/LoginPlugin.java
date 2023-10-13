package com.walker.socket.server.chat.plugin;

import com.walker.core.exception.ErrorException;
import com.walker.core.mode.Bean;
import com.walker.core.mode.BeanLinked;
import com.walker.socket.frame.Session;
import com.walker.socket.model.Msg;
import com.walker.socket.model.UserSocket;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;

public class LoginPlugin extends Plugin<LoginPlugin.User> {
    @Data
    @Accessors(chain = true)
    public static class User {
        String userid;
        String name;
        String pwd;
    }

    @Override
    public Class<User> getMsgDataClass() {
        return User.class;
    }

    @Override
    public User getMsgDataEg() {
        User eg = new User();
        eg.setUserid("001");
        eg.setName("001");
        eg.setPwd("***");
        return eg;
    }
    public LoginPlugin(Bean params) {
		super(params);
	}

    /**
     * {type:login,sto:,sfrom:128.2.3.1\:9080,from:,to:,data:{user:123,pwd:123456} }
     */
    @Override
    void onData(BeanLinked pluginParams, Msg msg, User user) {
        //以user 为 id去重 校验
        Session<?, Msg> session = getSession(msg.getFromSocketKey(), "");
        Session<?, Msg> sessionId = getSession("", user.userid);
        if(session == null) {
            throw new ErrorException("该用户已掉线" + user);
        }else {
            //查表? token 验证机制 公钥 私钥
            if(sessionId == null) { 	//初次登录
                session.setUserSocket(new UserSocket().setId(user.userid).setName(user.name).setPwd(user.pwd)); //存储用户信息到session
                session.login();
                msg.setToUserId(Arrays.asList(user.userid));
                msg.setSysInfo("初次登录ok");
                publish(msg);
            }else{
                msg.setSysInfo("已经登录");
                msg.setToUserId(Arrays.asList(user.userid, sessionId.getUserSocket().getId()));
                publish(msg);
            }
        }
    }



}
