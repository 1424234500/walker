package com.walker.controller.user;


import com.walker.Response;
import com.walker.mode.Page;
import com.walker.mode.User;
import com.walker.service.BaseService;
import com.walker.service.UserService;
import com.walker.util.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/*
测试 jap userService

 */
@Api(value = "service层 USER 实体类对象 ")
@Controller
@RequestMapping("/user")
public class UserController {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "SEX", required = false, defaultValue = "") String sex,
            @RequestParam(value = "NICK_NAME", required = false, defaultValue = "") String nickName,
            @RequestParam(value = "SIGN", required = false, defaultValue = "") String sign,
            @RequestParam(value = "EMAIL", required = false, defaultValue = "") String email,
            @RequestParam(value = "MOBILE", required = false, defaultValue = "") String mobile,
            @RequestParam(value = "AREA_ID", required = false, defaultValue = "") String areaId,
            @RequestParam(value = "DEPT_ID", required = false, defaultValue = "") String deptCode,
            @RequestParam(value = "PWD", required = false, defaultValue = "") String pwd
    ) {
        User user = new User();
        user.setID(id);
        user.setS_MTIME(TimeUtil.getTimeYmdHms());
        user.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        user.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        user.setNAME(name);
        user.setSEX(sex);
        user.setNICK_NAME(nickName);
        user.setSIGN(sign);
        user.setEMAIL(email);
        user.setMOBILE(mobile);
        user.setDEPT_ID(deptCode);
        user.setAREA_ID(areaId);
        user.setPWD(pwd);

        String info = "post user:" +user.toString();
        List<User> res = userService.saveAll(Arrays.asList(user));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = false, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
        Object res = userService.deleteAll(Arrays.asList(ids.split(",")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/get.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        User model = userService.get(new User().setID(id));
        return Response.makeTrue(info, model);
    }

    @ApiOperation(value = "get findPage 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "SEX", required = false, defaultValue = "") String sex,
            @RequestParam(value = "NICK_NAME", required = false, defaultValue = "") String nickName,
            @RequestParam(value = "SIGN", required = false, defaultValue = "") String sign,
            @RequestParam(value = "EMAIL", required = false, defaultValue = "") String email,
            @RequestParam(value = "MOBILE", required = false, defaultValue = "") String mobile,
            @RequestParam(value = "AREA_ID", required = false, defaultValue = "") String areaId,
            @RequestParam(value = "DEPT_ID", required = false, defaultValue = "") String deptCode,
            @RequestParam(value = "PWD", required = false, defaultValue = "") String pwd,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        User user = new User();
        user.setID(id);
        user.setS_MTIME(sMtime);
        user.setS_ATIME(sAtime);
        user.setS_FLAG(sFlag);
        user.setNAME(name);
        user.setSEX(sex);
        user.setNICK_NAME(nickName);
        user.setSIGN(sign);
        user.setEMAIL(email);
        user.setMOBILE(mobile);
        user.setDEPT_ID(deptCode);
        user.setAREA_ID(areaId);
        user.setPWD(pwd);

        String info = "get   user:" + user;

        List<User> list = userService.finds(user, page);
        page.setNum(userService.count(user));
        return Response.makePage(info, page, list);
    }


}
