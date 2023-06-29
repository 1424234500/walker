package com.walker.core.mode.sys;

import lombok.Data;

@Data
public class DataNormal implements DataPublish {
    String type = "text";    //消息体类型
    String body = "world";    //消息体详情
    String title = "hello";    //消息体标题


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getBody() {
        return body;
    }
}
