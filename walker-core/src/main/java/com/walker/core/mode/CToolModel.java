package com.walker.core.mode;

import com.alibaba.fastjson.JSON;
import com.walker.util.LangUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一个工具
 * 约束描述可存表
 * 根据约束寻址调用(传入前端参数)
 * 根据该记录渲染通用化前端界面
 * 前端调用后端获取结果
 */
@Data
public class CToolModel {
    public String ID = LangUtil.getGenerateId();

    public String KEY = "json"; //英文名
    public String NAME = "Json格式化"; //中文名
    public String INFO = "主要负责json格式化 序列化功能"; //详细说明
    public String URL_ICON = "https://img.alicdn.com/tfs/TB13DzOjXP7gK0jSZFjXXc5aXXa-212-48.png";  //icon地址
    public String URL = "http://127.0.0.1:8090/#/users/dept";  //功能前端地址
    public String TYPE = "test";   //工具类别


    public String METHOD = "Tools.out"; //后端工具接口 包.类.函数 调用路径
    public String METHOD_ARGS = JSON.toJSONString(new CKey());        //参数约束 json存储

    public List<CKey> args = new ArrayList<>(Arrays.asList(new CKey()));   //参数格式化 方便前端录入


//    @ApiOperation(value = "远程执行tool工具", notes = "post参数 RequestParam ")
//    @ResponseBody
//    @RequestMapping(value = "/tool.do", method = RequestMethod.POST, produces = "application/json")
//    public Response add(@RequestBody CInputModel param) {
//        return Response.makeTrue("", ClassUtil.doPackage(param.getURL(), param.getArgs().stream().map(item ->item.getKEY()).collect(Collectors.toList()).toArray()));
//    }



    /**
     * 一个参数的描述
     */
    @Data
    public static class CKey {
        public String KEY = "str"; //英文名
        public String NAME = "字符串"; //中文名
        public String INFO = "需要格式化的字符串"; //详细说明

        public String CLASS = "String";         //参数java类型
        public String VALUE = "{'hhh':'lll'}"; //前端录入的值

//    其他描述约束

    }


}


