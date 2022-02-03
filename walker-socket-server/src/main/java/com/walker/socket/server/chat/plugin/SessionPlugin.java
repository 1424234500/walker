package com.walker.socket.server.chat.plugin;

import com.walker.mode.Page;
import com.walker.mode.Msg;
import com.walker.socket.server.chat.handler.HandlerSessionArpListImpl;
import com.walker.mode.Bean;
import com.walker.mode.BeanLinked;

/**
 * 会话列表
 * @author walker
 */
public class SessionPlugin extends Plugin<Page> {

    @Override
    public Class<Page> getMsgDataClass() {
        return Page.class;
    }

    @Override
    public Page getMsgDataEg() {
        return new Page();
    }

    public SessionPlugin(Bean params) {
		super(params);
	}

    @Override
    void onData(BeanLinked pluginParams, Msg msg, Page data) {
        Object res = HandlerSessionArpListImpl.getInstance().getSessionList();
        msg.setRes(res);
        msg.setStatus(1);

        publish(msg);
    }


}
