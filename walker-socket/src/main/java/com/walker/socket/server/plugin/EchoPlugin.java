package com.walker.socket.server.plugin;

import com.walker.socket.base.DataNormal;
import com.walker.socket.base.Msg;
import com.walker.util.Bean;
import com.walker.util.BeanLinked;

public class EchoPlugin extends PluginAdapterDataNormal {

    EchoPlugin(Bean params) {
		super(params);
	}

    @Override
    void onData(BeanLinked pluginParams, Msg msg, DataNormal data) {
        msg.setRes("echo");
        publish(msg);
    }
}
