package com.walker.socket.server.plugin;

import com.walker.mode.DataNormal;
import com.walker.mode.Msg;
import com.walker.util.Bean;
import com.walker.util.BeanLinked;

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
