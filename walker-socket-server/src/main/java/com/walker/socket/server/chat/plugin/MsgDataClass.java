package com.walker.socket.server.chat.plugin;


/**
 *
 */
public interface MsgDataClass<MSGDATA> {
    Class<MSGDATA> getMsgDataClass();
    MSGDATA getMsgDataEg();
}