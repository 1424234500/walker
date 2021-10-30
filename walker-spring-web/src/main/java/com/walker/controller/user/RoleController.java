package com.walker.controller.user;


import com.walker.Response;
import com.walker.core.mode.Page;
import com.walker.mode.Role;
import com.walker.mode.RoleUser;
import com.walker.service.BaseService;
import com.walker.service.RoleService;
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

import java.util.*;

/**
 *  roleService
 */
@Api(value = "service层 DEPT 实体类对象 ")
@Controller
@RequestMapping("/role")
public class RoleController {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    @Autowired
    @Qualifier("roleService")
    private RoleService roleService;


    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,

            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,

            @RequestParam(value = "NUM", required = false, defaultValue = "") String num,
            @RequestParam(value = "LEVEL", required = false, defaultValue = "") String level

            ) {
        Role role = new Role();
        role.setID(id);
        role.setS_MTIME(TimeUtil.getTimeYmdHms());
        role.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        role.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        role.setNAME(name);
        role.setNUM(num);
        role.setLEVEL(level);

        String info = "post role:" +role.toString();
        List<Role> res = roleService.saveAll(Arrays.asList(role));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = false, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
        Object res = roleService.deleteAll(Arrays.asList(ids.split(",")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/get.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        Role model = roleService.get(new Role().setID(id));
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

            @RequestParam(value = "NUM", required = false, defaultValue = "") String num,
            @RequestParam(value = "LEVEL", required = false, defaultValue = "") String level,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        Role role = new Role();
        role.setID(id);
        role.setS_MTIME(sMtime);
        role.setS_ATIME(sAtime);
        role.setS_FLAG(sFlag);
        role.setNAME(name);
        role.setNUM(num);
        role.setLEVEL(level);

        String info = "get   role:" + role;

        List<Role> list = roleService.finds(role, page);
        page.setNum(roleService.count(role));
        return Response.makePage(info, page, list);
    }


    @ApiOperation(value = "查询user/dept id 关联所有的角色 是否包含未拥有的角色", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getRoles.do", method = RequestMethod.GET)
    public Response getUserRoles(
            @RequestParam(value = "ID", required = true, defaultValue = "") String id,
            @RequestParam(value = "DEPT_ID", required = false, defaultValue = "") String deptId,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag
    ) {
        String info = "ID:" + id + " DEPT_ID:" + deptId + " S_FLAG:" + sFlag;
        Map<String, Object> res = new HashMap<>();
        if(id.length() > 0) {
            List<Role> list = roleService.getRoles(id, sFlag);
            res.put("listUser", list);
        }
        if(deptId.length() > 0) {
            List<Role> listDept = roleService.getRoles(deptId, "1");
            res.put("listDept", listDept);
        }
        return Response.makeTrue(info, res);
    }
    @ApiOperation(value = "查询dept id 关联所有的角色 是否包含未拥有的角色", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getDeptRoles.do", method = RequestMethod.GET)
    public Response getDeptRoles(
            @RequestParam(value = "ID", required = true, defaultValue = "") String id,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag
    ) {
        String info = "ID:" + id + " S_FLAG:" + sFlag;
        Map<String, Object> res = new HashMap<>();
        if(id.length() > 0) {
            List<Role> listDept = roleService.getRoles(id, sFlag);
            res.put("listDept", listDept);
        }
        return Response.makeTrue(info, res);
    }


    @ApiOperation(value = "保存用户角色 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/saveRoles.do", method = RequestMethod.GET)
    public Response saveRoles(
            @RequestParam(value = "ID", required = true, defaultValue = "") String id,
            @RequestParam(value = "ON", required = false, defaultValue = "") String on,
            @RequestParam(value = "OFF", required = false, defaultValue = "") String off
    ) {
        String info = "ID:" + id + " ON:" + on + " OFF:" + off;
        List<String> listOn = on.length() > 0 ? Arrays.asList(on.split(",")) : new ArrayList<>();
        List<String> listOff = off.length() > 0 ? Arrays.asList(off.split(",")) : new ArrayList<>();
        if(listOn.size() == 0 && listOff.size() == 0){
            return Response.makeFalse(info + " all is null ");
        }
        List<RoleUser> roleUserList = roleService.saveRoles(id, listOn, listOff);

        return Response.makeTrue(info, roleUserList);
    }


}
