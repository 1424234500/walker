package com.walker;

import com.walker.config.Context;
import com.walker.core.mode.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@ApiModel(value = "基础返回类value", description = "基础返回类description")
@Data
//@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","fieldHandler"})   //使用jackjson将对象json化的时候出现的错误。
public class Response implements Serializable {

    public static final String INFO_TRUE = "操作成功";
    public static final String INFO_FALSE = "操作失败";


    @ApiModelProperty(example = "true")
    Boolean flag;

    @ApiModelProperty(example = "操作成功")
    String info;

    @ApiModelProperty(example = "128")
    Long costTime;

    @ApiModelProperty(example = "{id:1, name:2}")
    Object data;


    public Response() {
    }

    Response(Boolean flag, String info, Object data) {
        this.flag = flag;
        this.info = info;
//        this.costTime = costTime;
        this.data = data;
    }

    public static String getInfoTrue() {
        return INFO_TRUE;
    }

    public static String getInfoFalse() {
        return INFO_FALSE;
    }

    public static Response make(Boolean sta, String info, Object data) {
        long timestart = Context.getTimeStart();
        long timestop = System.currentTimeMillis();
        long time = timestop - timestart;
        return new Response(sta, info, data).setCostTime(time);
    }

    public static Response makeTrue(String info) {
        return make(true, info, null);
    }

    public static Response makeTrue(String info, Object data) {
        return make(true, info, data);
    }

    public static Response makeFalse(String info) {
        return make(false, info, null);
    }

    public static Response makeFalse(String info, Object data) {
        return make(false, info, data);
    }

    public static Response makePage(String info, Page page, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("data", data);
        return make(true, info, map);
    }

    public Boolean getFlag() {
        return flag;
    }

    public Response setFlag(Boolean flag) {
        this.flag = flag;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public Response setInfo(String info) {
        this.info = info;
        return this;
    }

    public Long getCostTime() {
        return costTime;
    }

    public Response setCostTime(Long costTime) {
        this.costTime = costTime;
        return this;
    }

    public <T> T getData() {
        return (T) data;
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }

    public String toString() {
        return Arrays.toString(new Object[]{"Response", flag, info, data});
    }


}
