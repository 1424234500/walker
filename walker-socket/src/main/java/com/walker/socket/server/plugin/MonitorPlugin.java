package com.walker.socket.server.plugin;

import com.walker.socket.base.DataNormal;
import com.walker.socket.base.Msg;
import com.walker.socket.server.handler.HandlerSessionArpListImpl;
import com.walker.util.Bean;
import com.walker.util.BeanLinked;

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
