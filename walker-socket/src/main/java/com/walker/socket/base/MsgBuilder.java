package com.walker.socket.base;

import com.walker.socket.server.plugin.LoginPlugin;
import com.walker.socket.server.plugin.MonitorPlugin;
import com.walker.socket.server.plugin.PluginAdapterDataNormal;
import com.walker.socket.server.plugin.SessionPlugin;
import com.walker.util.LangUtil;
import com.walker.util.Tools;

/**
 * 构造各种消息格式
 *
 * @author walker
 */
public class MsgBuilder {
//	16:38:35.main-1.{"DATA":{"pwd":"123456","user":"test"},"TYPE":Plugin.KEY_LOGIN,"TC":1560415115280}
//	16:38:35.main-1.{"DATA":"{TYPE:text,TEXT:test}","TO":"to","TYPE":"message","TC":1560415115610}
//	16:38:35.main-1.{"TYPE":"monitor","TC":1560415115611}

    /**
     * "{type:login,data:{user:username,pwd:123456} }"
     */
    public static Msg makeLogin(String id) {
        LoginPlugin.Data data = new LoginPlugin(null).getMsgDataEg();
        data.setUserid(id);
        return make("", data, "", "login");
    }

    /**
     * "{type:monitor,data:{} }"
     */
    public static Msg testMonitor() {
        return make("", new MonitorPlugin(null).getMsgDataEg(), "", "monitor");
    }

    /**
     * "{type:message,to:all_socket/all_user/000,data:{type:txt,body:body} }"
     */
    public static Msg makeMessageAllSocket(String json) {
        return make(json, "all_socket", "message");
    }

    public static Msg makeMessageAllUser(String json) {
        return make(json, "all_user", "message");
    }

    public static Msg make(String json, String to, String plugin) {
        DataNormal dataNormal = new PluginAdapterDataNormal(null).getMsgDataEg();
        if (!json.startsWith("{")) {
            dataNormal.setBody(json);
        }
        return make(json, dataNormal, to, plugin);
    }

    public static Msg make(String json, Object data, String to, String plugin) {
        Msg msg;
        if (json.startsWith("{")) {
            msg = Msg.parse(json);
        } else {
            msg = new Msg();
        }
        if (msg.getData() == null) {
            msg.setData(data);
        }
        if (msg.getToUserId().size() == 0 && to.length() > 0) {
            msg.getToUserId().add(to);
        }
        if (msg.getPlugin().length() == 0) {
            msg.setPlugin(plugin);
        }
        msg.setTimeClientSend(System.currentTimeMillis());

        if (msg.getIdFromClient().length() == 0) {
            msg.setIdFromClient(LangUtil.getGenerateId());
        }

        return msg;
    }


    public static Msg makeSession() {
        return make("", new SessionPlugin(null).getMsgDataEg(), "", "session");
    }


    public static Msg makeException(Msg msg, Exception e) {
        msg.setSysInfo(e.getMessage());
        msg.setRes(Tools.toString(e));
        return msg;
    }

}
