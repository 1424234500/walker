package com.walker.controller;


import com.walker.Response;
import com.walker.core.aop.FunArgsReturn;
import com.walker.core.mode.Page;
import com.walker.dao.ConfigDao;
import com.walker.dao.JdbcDao;
import com.walker.mode.SysConfig;
import com.walker.service.BaseService;
import com.walker.service.CacheService;
import com.walker.service.SysConfigService;
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
 * 任务调度配置
 */
@Api(value = "配置中心")
@Controller
@RequestMapping("/sysConfig")
public class SysConfigController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;
    @Autowired
    @Qualifier("cacheService")
    private CacheService cacheService;
    @Autowired
    @Qualifier("jdbcDao")
    private JdbcDao jdbcDao;
    @Autowired
    @Qualifier("sysConfigService")
    private SysConfigService sysConfigService;
    @Autowired
    @Qualifier("configDao")
    private ConfigDao configDao;

    @ApiOperation(value = "刷新配置单项缓存 或 更新所有配置缓存", notes = "now")
    @ResponseBody
    @RequestMapping(value = "/refresh.do", method = RequestMethod.GET)
    public Response refresh(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id
    ) throws Exception {
        Object res = null;
        if(id.length() > 0){
            res = configDao.set(id, new FunArgsReturn<String, Integer>() {
                @Override
                public Integer make(String obj) {
                    return 1;
                }
            });
        }else{
            res = configDao.reload();
        }
        return Response.makeTrue(id.length() > 0 ? "reload " + id : "reload all", res);
    }
    @ApiOperation(value = "刷新配置单项缓存 或 更新所有配置缓存", notes = "now")
    @ResponseBody
    @RequestMapping(value = "/load.do", method = RequestMethod.GET)
    public Response load(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id
    ) throws Exception {
        String res = "";
        if(id.length() > 0) {
            res = configDao.get(id, "");
        } else{
            res = "no key ?";
        }
        return Response.make(res.length() > 0, id, res);
    }

    @ApiOperation(value = "保存配置", notes = "通用存储")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "1") String sFlag,
            @RequestParam(value = "VALUE", required = false, defaultValue = "") String value,
            @RequestParam(value = "ABOUT", required = false, defaultValue = "") String about

    ) throws Exception {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setID(id);
        sysConfig.setS_MTIME(sMtime.length() == 0 ? TimeUtil.getTimeYmdHmss() : sMtime);
        sysConfig.setS_FLAG(sFlag);
        sysConfig.setVALUE(value);
        sysConfig.setABOUT(about);
        Integer res = configDao.set(id, new FunArgsReturn<String, Integer>() {
            @Override
            public Integer make(String obj) {
                return sysConfigService.saveAll(Arrays.asList(sysConfig)).size();
            }
        });
//        List<SysConfig> res = sysConfigService.saveAll(Arrays.asList(sysConfig));
        return Response.makeTrue("", res);
    }

    @ApiOperation(value = "删除配置及其缓存", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id
    ) throws Exception {
        if(id.length() == 0){
            return Response.makeFalse("null ? args");
        }
        String ids[] = id.split(",");
        Integer res = configDao.set(id, new FunArgsReturn<String, Integer>() {
            @Override
            public Integer make(String obj) {
                return sysConfigService.deleteAll(Arrays.asList(ids))[0];
            }
        });
//        Object res = sysConfigService.deleteAll(Arrays.asList(ids));
        return Response.makeFalse( "", res);
    }
    @ApiOperation(value = "分页查询任务列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag,
            @RequestParam(value = "VALUE", required = false, defaultValue = "") String value,
            @RequestParam(value = "ABOUT", required = false, defaultValue = "") String about,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        SysConfig sysConfig = new SysConfig();
        sysConfig.setID(id);
        sysConfig.setS_MTIME(sMtime);
        sysConfig.setS_FLAG(sFlag);
        sysConfig.setVALUE(value);
        sysConfig.setABOUT(about);

        String info = "get   sysConfig :" + sysConfig;

        List<SysConfig> list = sysConfigService.finds(sysConfig, page);
        page.setNum(sysConfigService.count(sysConfig));
        return Response.makePage(info, page, list);
    }



}
