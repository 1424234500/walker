package com.walker.controller.user;


import com.walker.Response;
import com.walker.mode.Page;
import com.walker.mode.school.RoleUser;
import com.walker.service.RoleUserService;
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

/**
 *  roleUserService
 */
@Api(value = "service层 DEPT 实体类对象 ")
@Controller
@RequestMapping("/roleUser")
public class RoleUserController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("roleUserService")
    private RoleUserService roleUserService;


    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,

            @RequestParam(value = "ROLE_ID", required = false, defaultValue = "") String roleUserId,

            @RequestParam(value = "USER_ID", required = false, defaultValue = "") String userId

    ) {
        RoleUser roleUser = new RoleUser();
        roleUser.setID(id);
        roleUser.setS_MTIME(TimeUtil.getTimeYmdHms());
        roleUser.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        roleUser.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        roleUser.setROLE_ID(roleUserId);
        roleUser.setUSER_ID(userId);
        String info = "post roleUser:" +roleUser.toString();
        List<RoleUser> res = roleUserService.saveAll(Arrays.asList(roleUser));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = false, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
        Object res = roleUserService.deleteAll(Arrays.asList(ids.split(",")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/get.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        RoleUser model = roleUserService.get(new RoleUser().setID(id));
        return Response.makeTrue(info, model);
    }

    @ApiOperation(value = "get findPage roleUserUser 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag,

            @RequestParam(value = "ROLE_ID", required = false, defaultValue = "") String roleUserId,

            @RequestParam(value = "USER_ID", required = false, defaultValue = "") String userId,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        RoleUser roleUser = new RoleUser();
        roleUser.setID(id);
        roleUser.setS_MTIME(sMtime);
        roleUser.setS_ATIME(sAtime);
        roleUser.setS_FLAG(sFlag);
        roleUser.setROLE_ID(roleUserId);
        roleUser.setUSER_ID(userId);

        String info = "get   roleUser:" + roleUser;

        List<RoleUser> list = roleUserService.finds(roleUser, page);
        page.setNum(roleUserService.count(roleUser));
        return Response.makePage(info, page, list);
    }





}
