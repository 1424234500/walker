package com.walker.controller.user;


import com.walker.Response;
import com.walker.mode.Page;
import com.walker.mode.Area;
import com.walker.service.AreaService;
import com.walker.service.BaseService;
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
测试 jap areaService

 */
@Api(value = "controller层 Area 实体类对象 ")
@Controller
@RequestMapping("/area")
public class AreaController {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    @Autowired
    @Qualifier("areaService")
    private AreaService areaService;

    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "P_ID", required = false, defaultValue = "") String pid

            ) {
        Area area = new Area();
        area.setID(id);
        area.setS_MTIME(TimeUtil.getTimeYmdHms());
//        area.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        area.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        area.setNAME(name);
        area.setP_ID(pid);

        String info = "post area:" +area.toString();
        List<Area> res = areaService.saveAll(Arrays.asList(area));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ID", required = true, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
        if(ids == null || ids.length() <= 0)
            return Response.makeFalse("args is null ?");
        Object res = areaService.deleteAll(Arrays.asList(ids.split(",")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/get.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "ID", required = true) String id
    ) {
        String info = "get id:" + id;
        Area model = areaService.get(new Area().setID(id));
        if(model != null)
            return Response.makeTrue(info, model);
        else
            return Response.makeFalse(info);
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
            @RequestParam(value = "PATH_NAME", required = false, defaultValue = "") String pathName,
            @RequestParam(value = "CODE", required = false, defaultValue = "") String code,
            @RequestParam(value = "LEVEL", required = false, defaultValue = "") String level,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        if(pidNull.equalsIgnoreCase("true")){
            List<Area> list = areaService.findsRoot(page);

            return Response.makePage("", page, list);
        }

        Area area = new Area();
        area.setID(id);
        area.setS_MTIME(sMtime);
        area.setS_FLAG(sFlag);
        area.setNAME(name);
        area.setP_ID(pid);
        area.setPATH(path);
        area.setPATH_NAME(pathName);
        area.setCODE(code);
        area.setLEVEL(level);

        String info = "get   area:" + area;

        List<Area> list = areaService.finds(area, page);
        page.setNum(areaService.count(area));
        return Response.makePage(info, page, list);
    }


}
