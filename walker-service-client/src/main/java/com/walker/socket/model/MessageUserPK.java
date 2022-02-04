package com.walker.socket.model;

import java.io.Serializable;

public class MessageUserPK implements Serializable {
    private String id;
    private String msgId;

    @Override
    public String toString() {
        return "MessageUserPK{" +
                "id='" + id + '\'' +
                ", msgId='" + msgId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public MessageUserPK setId(String id) {
        this.id = id;
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public MessageUserPK setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }
}