package com.walker.socket.server.chat.plugin;

import com.walker.mode.sys.DataNormal;
import com.walker.socket.model.Msg;
import com.walker.mode.Bean;
import com.walker.mode.BeanLinked;

public  class MessagePlugin extends PluginAdapterDataNormal {


    MessagePlugin(Bean params) {
		super(params);
	}

    /**
     * {type:message,sto:,sfrom:128.2.3.1\:9080,to:123,from:222,data:{type:txt,body:hello} }
     */
    @Override
    void onData(BeanLinked pluginParams, Msg msg, DataNormal data) {

        //存储
//		 *					{type:message,data:{to:123,from:222,type:txt,body:hello} }
//		 *					message 发给user/group 请求转发
//		 *						data.to		发给目标用户	u_123,u_2323,g_xxx,s_all,s_online
//		 *						data.from	发送方来源	u_123,s_admin
//		 *						data.type	具体消息类型	text,img,voice,video,map
//		 *						data.body
        //发送方设置去向 接收方只看到发送给自己

        //离线消息记录

//	MessageService messageService = new MessageServiceImpl();
//		MessageService messageService = DubboMgr.getService("messageServiceSharding");
//		messageService.save(msg.getTo().split(", *"), msg);

        //广播
        publish(msg);
    }

}
