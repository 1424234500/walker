package com.walker.spring.controller;


import com.walker.core.mode.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/*

springmvc参数注入
@RequestParam、 @RequestBody、 @RequestHeader 、 @PathVariable

handler method 参数绑定常用的注解,我们根据他们处理的Request的不同内容部分分为四类：（主要讲解常用类型）
A、处理requet uri 部分（这里指uri template中variable，不含queryString部分）的注解：   @PathVariable;
B、处理request header部分的注解：   @RequestHeader, @CookieValue;
C、处理request body部分的注解：@RequestParam,  @RequestBody;
D、处理attribute类型是注解： @SessionAttributes, @ModelAttribute;

swagger配置UI界面
//接口参数配置 可依赖于springmvc自带配置 不要别名
@ApiImplicitParam(paramType="query", name = "id", value = "用户ID", required = true, dataType = "String"),
@ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "id", value = "用户ID", required = true, dataType = "String"),
        @ApiImplicitParam(paramType="query", name = "name", value = "名字"),
        @ApiImplicitParam(paramType="query", name = "time", value = "时间"),
})
//可配置别名 但一旦写了之后 旧不会使用springmvc自带的配置
RequestParam
    @AliasFor("name")
    String value() default "";
    @AliasFor("value")
    String name() default "";
    boolean required() default true;
    String defaultValue() default "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n";

Parameter Types
            OpenAPI 3.0 distinguishes between the following parameter types based on the parameter location. The location is determined by the parameter’s in key, for example, in: query or in: path.
        path parameters, such as /users/{id}
        query parameters, such as /users?role=admin
        header parameters, such as X-MyHeader: Value
        cookie parameters, which are passed in the Cookie header, such as Cookie: debug=0; csrftoken=BUSe35dohU3O1MZvDCU

//接口注释
@ApiOperation(value = "测试借口3", notes = "测试借口3", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)


 */
@Api(value = "测试 控制层 | swagger | springboot | controller | 返回对象 ")
@Controller
@RequestMapping("/restful")
public class RestfulController {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @ApiOperation(value = "get 获取字符串", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/{id}/{name}/make.do", method = RequestMethod.GET)
    public String get(
            @PathVariable(value = "id", required = true) String id,
            @PathVariable(value = "name", required = false) String name
    ) {
        String res = "get id:" + id + " name:" + name;
        log.info(res);
        return res;
    }

    @ApiOperation(value = "get 获取对象 自动Object get set 转json", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/{id}/{name}/makeobject.do", method = RequestMethod.GET)
    public Response getobject(
            @PathVariable(value = "id", required = true) String id,
            @PathVariable(value = "name", required = false) String name
    ) {
        String res = "get id:" + id + " name:" + name;
        return new Response().setRes(res);
    }

    @ApiOperation(value = "post 添加对象", notes = "post参数 RequestParam ")
    @ResponseBody
    @RequestMapping(value = "/make.do", method = RequestMethod.POST, produces = "application/json")
    public Response post(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = true, defaultValue = "nobody") String name,
            @RequestParam(value = "time", required = false, defaultValue = "000") String time
    ) {
        String res = "post id:" + id + " name:" + name + " time:" + time;
        return new Response().setRes(res);
    }

    @ApiOperation(value = "set 更新", notes = "put参数 RequestParam")
    @ResponseBody
    @RequestMapping(value = "/make.do", method = RequestMethod.PUT)
    public Response put(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default-name") String name,
            @RequestParam(value = "time", required = false, defaultValue = "000") String time
    ) {
        String res = "set id:" + id + " name:" + name + " time:" + time;
        return new Response().setRes(res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/{id}/make.do", method = RequestMethod.DELETE)
    public Response delete(
            @PathVariable(value = "id", required = true) String id
    ) {
        String res = "delete id:" + id;
        return new Response().setRes(res);
    }

}
