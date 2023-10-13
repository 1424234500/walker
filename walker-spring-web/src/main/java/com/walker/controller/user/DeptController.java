package com.walker.controller.user;


import com.walker.core.mode.Page;
import com.walker.core.mode.Response;
import com.walker.core.mode.school.Dept;
import com.walker.core.util.TimeUtil;
import com.walker.service.BaseService;
import com.walker.service.DeptService;
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
测试 jap deptService

 */
@Api(value = "service层 DEPT 实体类对象 ")
@Controller
@RequestMapping("/dept")
public class  DeptController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    @Autowired
    @Qualifier("deptService")
    private DeptService deptService;

    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "P_ID", required = false, defaultValue = "") String pid
//            @RequestParam(value = "PATH", required = false, defaultValue = "") String path

            ) {
        Dept dept = new Dept();
        dept.setID(id);
        dept.setS_MTIME(TimeUtil.getTimeYmdHms());
//        dept.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        dept.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        dept.setNAME(name);
        dept.setP_ID(pid);
//        dept.setPATH(path);

        String info = "post dept:" + dept;
        List<Dept> res = deptService.saveAll(Arrays.asList(dept));
        return new Response().setTip(info).setRes(res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = true, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
        if(ids == null || ids.length() <= 0)
            return new Response().setSuccess(false).setTip("args is null ?");
        Object res = deptService.deleteAll(Arrays.asList(ids.split(",")));
        return new Response().setTip(info).setRes(res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/get.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        Dept model = deptService.get(new Dept().setID(id));
        return new Response().setTip(info).setRes(model).setSuccess(model != null);
    }

    @ApiOperation(value = "get findPage 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag,

            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,

            @RequestParam(value = "P_ID_NULL", required = false, defaultValue = "false") String pidNull,
            @RequestParam(value = "P_ID", required = false, defaultValue = "") String pid,
            @RequestParam(value = "PATH", required = false, defaultValue = "") String path,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        if(pidNull.equalsIgnoreCase("true")){
            List<Dept> list = deptService.findsRoot(page);
            return new Response().setRes(list);
        }

        Dept dept = new Dept();
        dept.setID(id);
        dept.setS_MTIME(sMtime);
        dept.setS_FLAG(sFlag);
        dept.setNAME(name);
        dept.setP_ID(pid);
        dept.setPATH(path);

        String info = "get   dept:" + dept;

        List<Dept> list = deptService.finds(dept, page);
        return new Response().setTotal(deptService.count(dept)).setRes(list).setTip(info);
    }


}
