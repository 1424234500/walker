package com.walker.socket.frame;

import com.walker.core.route.SubPub;
import com.walker.core.route.SubPubMgr;
import com.walker.socket.model.UserSocket;
import org.apache.log4j.Logger;

/**
 * 会话 关联socket userSocket
 * <p>
 * 订阅模式管理分发
 * <p>
 * 建立连接 订阅socket
 * 登录成功 订阅user
 * 订阅到消息 写入socket
 * 退出登录	取消订阅user
 * 断开连接 取消订阅socket
 */
public abstract class Session<SOCKET, DATA> implements SubPub.OnSubscribe<DATA, Session<SOCKET, DATA>> {
    //  socket连接信息
    public SOCKET socket;
    protected Logger log = Logger.getLogger(Session.class);
    //	用户信息
    UserSocket userSocket;
    /**
     * 路由 发布订阅
     */
    SubPub<DATA, Session<SOCKET, DATA>> sub = SubPubMgr.getSubPub("msg_route_server", 0);

    public Session(SOCKET socket) {
        this.socket = socket;
    }

    public UserSocket getUserSocket() {
        return userSocket;
    }

    public Session<SOCKET, DATA> setUserSocket(UserSocket userSocket) {
        this.userSocket = userSocket;
        return this;
    }

    public SOCKET getSocket() {
        return socket;
    }

    //uuid socket
    public abstract String key();

    //用于session自己订阅写入消息
    public abstract void send(DATA data) throws Exception;

    @Override
    public String toString() {
        return "Socket[" + key() + "@" + userSocket + "]";
    }

    public Boolean isLogin() {
        return getUserSocket().getId().length() != 0;
    }

    /**
     * 长连接成功后 订阅socket消息
     */
    public void onConnect() {
        sub.subscribe(key(), this);    //订阅当前socket
        sub.subscribe("all_socket", this);        //订阅所有socket
//		this.id = key();
    }

    public void onUnConnect() {
        sub.unSubscribe(key(), this);    //订阅当前socket
        sub.unSubscribe("all_socket", this);    //订阅所有socket
    }

    /**
     * 登录成功后 订阅用户消息 单聊群聊特殊规则
     * 注册Rarp ip -> session
     */
    public void login() {
        this.setUserSocket(this.unLogin());
        sub.subscribe(this.userSocket.getId(), this);    //订阅当前登录用户userid
        sub.subscribe("all_user", this);        //订阅所有登录用户
        log.info("login ok " + this);
    }

    public UserSocket unLogin() {
        sub.unSubscribe(userSocket.getId(), this);    //订阅当前登录用户userid
        sub.unSubscribe("all_user", this);        //订阅所有登录用户
        UserSocket userSocket = this.userSocket;
        this.userSocket = null;
        log.info("unlogin ok " + this);
        return userSocket;
    }

    /**
     * session负责自己处理业务
     */
    @Override
    public SubPub.Res<Session<SOCKET, DATA>> onSubscribe(DATA msg) {
        try {
            send(msg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new SubPub.Res<>(Type.DONE, this);
    }


}
