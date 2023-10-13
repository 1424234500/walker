package com.walker.controller;


import com.walker.config.ShiroConfig;
import com.walker.core.mode.Response;
import com.walker.core.mode.school.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

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
@Api(value = "shiro权限控制 未登录 登录 管理员")
@Controller
@RequestMapping("/shiro")
public class ShiroController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ShiroConfig shiroConfig;

    @ApiOperation(value="登录shiro")
    @ResponseBody
    @RequestMapping(value="/login.do",method= RequestMethod.POST)
    public Response login(
            @RequestParam(value = "username", required = true, defaultValue = "guest") String username,
            @RequestParam(value = "password", required = true, defaultValue = "") String password
    ){
//            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
//            SecurityUtils.getSubject().login(token);
//            WebUser webUser = (WebUser) SecurityUtils.getSubject().getPrincipal();//登录成功之后，取出用户信息放进session存储域

        User user = new User().setNAME(username).setID(username).setPWD(password).setSIGN("sign");

        String token = shiroConfig.onlineUser(user);
        Map<String, Object> res = new HashMap<>();
        res.put("USER", user);
        res.put("TOKEN", token);
        return new Response().setSuccess(token.length() > 0).setTip(token.length() > 0 ? "登录成功" : "登录失败 " + username).setRes(res);
    }



    @ApiOperation(value="游客")
    @ResponseBody
    @RequestMapping(value="/getGuest.do",method= RequestMethod.GET)
    public String getGuest(){
        log.info("guest");
        return "guest";
    }
    @ApiOperation(value="普通用户")
    @ResponseBody
    @RequestMapping(value="/getNormal.do",method= RequestMethod.GET)
    public String getNormal(){
        log.info("normal");
        return "normal";
    }
    @ApiOperation(value="管理员")
    @ResponseBody
    @RequestMapping(value="/getAdmin.do",method= RequestMethod.GET)
    public String getAdmin(){
        log.info("admin");
        return "admin";
    }

}
