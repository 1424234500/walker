package com.walker.socket.server.chat.plugin;

import com.walker.core.mode.Bean;
import com.walker.core.mode.BeanLinked;
import com.walker.core.mode.sys.DataNormal;
import com.walker.socket.model.Msg;
import com.walker.socket.server.chat.handler.HandlerSessionArpListImpl;

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
