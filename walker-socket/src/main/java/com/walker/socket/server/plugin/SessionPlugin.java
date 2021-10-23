package com.walker.socket.server.plugin;

import com.walker.core.mode.Page;
import com.walker.socket.base.Msg;
import com.walker.socket.server.handler.HandlerSessionArpListImpl;
import com.walker.util.Bean;
import com.walker.util.BeanLinked;

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
