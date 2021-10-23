package com.walker.socket.base.impl;

import com.walker.socket.base.Session;
import com.walker.socket.base.encode.DataEncodeDecode;

/**
 * 使用future模板控制socket事件启停
 * <p>
 * 底层实现详情
 */

public abstract class ServerSessionEncode<SOCKET, DATA> extends ServerSession<SOCKET, DATA> implements DataEncodeDecode<DATA> {


    /**
     * 底层编码解码
     */
    private DataEncodeDecode<DATA> dataEncodeDecode;

    public ServerSessionEncode(int port) {
        super(port);
    }


    public ServerSessionEncode(String ip, int port, String name) {
        super(ip, port, name);
    }

    public ServerSessionEncode<SOCKET, DATA> setEncodeDecode(DataEncodeDecode<DATA> dataEncodeDecode) {
        this.dataEncodeDecode = dataEncodeDecode;
        return this;
    }

    @Override
    public String encode(DATA data) {
        return dataEncodeDecode.encode(data);
    }

    @Override
    public DATA decode(String str) {
        return dataEncodeDecode.decode(str);
    }

    /**
     * 适配编码发送重载
     */
    public void sendAutoDecode(Session<SOCKET, DATA> session, String str) throws Exception {
        super.send(session, decode(str));
    }

    /**
     * 读取流
     */
    @Override
    public DATA read(Session<SOCKET, DATA> session) throws Exception {
        String readLine = readString(session.getSocket());
        if (readLine == null || readLine.length() == 0) {
            return null;
        }
        return dataEncodeDecode.decode(readLine);
    }

    public abstract String readString(SOCKET socket) throws Exception;

    /**
     * 写入流
     */
    @Override
    public void write(Session<SOCKET, DATA> session, DATA data) throws Exception {
        if (data != null) {
            writeString(session.getSocket(), dataEncodeDecode.encode(data));
        }
    }

    public abstract void writeString(SOCKET socket, String data) throws Exception;


}
