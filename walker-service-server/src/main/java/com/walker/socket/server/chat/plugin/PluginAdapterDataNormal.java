package com.walker.socket.server.chat.plugin;

import com.walker.core.mode.Bean;
import com.walker.core.mode.BeanLinked;
import com.walker.core.mode.sys.DataNormal;
import com.walker.socket.model.Msg;

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
