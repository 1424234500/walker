package com.walker.controller;

import com.walker.core.database.ZooKeeperUtil;
import com.walker.core.database.ZookeeperConnector;
import com.walker.core.mode.Page;
import com.walker.core.mode.Response;
import com.walker.core.mode.SqlColumn;
import com.walker.service.RedisService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/zookeeper"})
public class ZookeeperController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisService redisService;

    ZookeeperConnector zookeeperConnector = new ZookeeperConnector().setConnectString("localhost:8096").setSessionTimeout(10000);


    @ApiOperation(value = "节点列表查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response locks(
            @RequestParam(value = "URL", required = false, defaultValue = "/") String url,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        List<Map<String, String>> list = new ArrayList<>();

        List<?> res = zookeeperConnector.findPage(url, page);
        page.setTotal(res.size());
        return new Response().setTip("get zk value").setTotal(page.getTotal()).setRes(res);
    }

    @ApiOperation(value = "删除zk", notes = "")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = true, defaultValue = "") String ids
    ) {

        long res = 0;
        for(String id : ids.split(",")){
            try {
                res += zookeeperConnector.doZooKeeper(zk -> ZooKeeperUtil.delete(zk, false, new HashSet<String>(Arrays.asList(ids.split(",")))));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return new Response().setTip("rm zks " + ids).setRes(res);
    }
    @ApiOperation(value = "添加字节点/更新 zk key value", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "URL", required = true, defaultValue = "/") String url
            , @RequestParam(value = "DATA", required = false, defaultValue = "") String data
            , @RequestParam(value = "CHILD_SIZE", required = false, defaultValue = "") String childSize
            , @RequestParam(value = "FROM_URL", required = false, defaultValue = "/") String fromUrl
            , @RequestParam(value = "SEARCH_URL", required = false, defaultValue = "/") String searchUrl
    ) {
        url = "/" + searchUrl + "/" + url;
        while(url.indexOf("//") >= 0){
            url = url.replace("//", "/");
        }

        String finalUrl = url;
        return new Response().setTip("save zk " + url + " " + data).setRes(zookeeperConnector.doZooKeeper(zk -> ZooKeeperUtil.createOrUpdateVersion(zk, false, finalUrl, data)));
    }


    @ApiOperation(value = "获取需要前段展示的表列名 备注名", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap(
    ) {
        List<SqlColumn> colMap = zookeeperConnector.getCols();
        Map<String, Object> res = new HashMap<>();
        res.put("colMap", colMap);
        res.put("colKey", colMap.stream().map(item -> item.getColumnName()).collect(Collectors.toList()));
        return new Response().setTip("zk key value").setRes(res);
    }


}
