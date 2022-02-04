package com.walker.socket.server.chat.plugin;

import com.walker.mode.sys.DataNormal;
import com.walker.socket.model.Msg;
import com.walker.mode.Bean;
import com.walker.mode.BeanLinked;

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
