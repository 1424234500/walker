package com.walker.socket.server.plugin;


/**
 *
 */
public interface MsgDataClass<MSGDATA> {
    Class<MSGDATA> getMsgDataClass();
    MSGDATA getMsgDataEg();
}