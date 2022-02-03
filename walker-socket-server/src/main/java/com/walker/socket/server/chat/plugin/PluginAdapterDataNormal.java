package com.walker.socket.server.chat.plugin;

import com.walker.mode.DataNormal;
import com.walker.mode.Msg;
import com.walker.mode.Bean;
import com.walker.mode.BeanLinked;

public class PluginAdapterDataNormal extends Plugin<DataNormal> {

    public PluginAdapterDataNormal(Bean params) {
        super(params);
    }

    @Override
    public Class<DataNormal> getMsgDataClass() {
        return DataNormal.class;
    }

    @Override
    public DataNormal getMsgDataEg() {
        return new DataNormal();
    }

    @Override
    void onData(BeanLinked pluginParams, Msg msg, DataNormal dataNormal) {

    }

}
