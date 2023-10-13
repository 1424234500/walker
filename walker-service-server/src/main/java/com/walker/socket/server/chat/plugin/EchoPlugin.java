package com.walker.socket.server.chat.plugin;

import com.walker.core.mode.Bean;
import com.walker.core.mode.BeanLinked;
import com.walker.core.mode.sys.DataNormal;
import com.walker.socket.model.Msg;

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
