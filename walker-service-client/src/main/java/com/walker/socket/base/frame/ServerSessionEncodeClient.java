package com.walker.socket.base.frame;

import com.walker.socket.base.Session;
import com.walker.socket.client.frame.Out;

/**
 * 使用future模板控制socket事件启停
 * <p>
 * 底层实现详情
 */

public abstract class ServerSessionEncodeClient<SOCKET, DATA> extends ServerSessionEncode<SOCKET, DATA> {


    //    客户端依赖输出图形化包装
    Out out;
    //    客户端自持有session send 同名扩展重载
    Session<SOCKET, DATA> session;


    public ServerSessionEncodeClient(int port) throws Exception {
        super(port);
    }

    public ServerSessionEncodeClient(String ip, int port, String name) {
        super(ip, port, name);
    }

    public Out getOut() {
        return out;
    }

    public ServerSessionEncodeClient<SOCKET, DATA> setOut(Out out) {
        this.out = out;
        return this;
    }

    @Override
    public void onNewConnection(Session<SOCKET, DATA> session) {
        this.session = session;
        super.onNewConnection(session);
    }

    public DATA read() throws Exception {
        return super.readSession(session);
    }

    public void send(DATA str) throws Exception {
        super.send(session, str);
    }

    public void sendAutoDecode(String str) throws Exception {
        super.sendAutoDecode(session, str);
    }

    public void onReceive(DATA str) throws Exception {
        super.onReceive(session, str);
    }

    public void onDisConnection() {
        super.onDisConnection(session);
    }
}