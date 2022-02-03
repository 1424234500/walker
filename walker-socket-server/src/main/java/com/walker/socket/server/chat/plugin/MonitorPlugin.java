package com.walker.socket.server.chat.plugin;

import com.walker.mode.DataNormal;
import com.walker.mode.Msg;
import com.walker.socket.server.chat.handler.HandlerSessionArpListImpl;
import com.walker.mode.Bean;
import com.walker.mode.BeanLinked;

/**
 * 数据监控工具
 * @author walker
 *
 */
public class MonitorPlugin extends PluginAdapterDataNormal {

    public MonitorPlugin(Bean params) {
		super(params);
	}

    @Override
    void onData(BeanLinked pluginParams, Msg msg, DataNormal data) {
        Object res = HandlerSessionArpListImpl.getInstance().show();
        msg.setRes(res);
        publish(msg);
    }

}
