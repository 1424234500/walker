package com.walker.controller;

import com.walker.Response;
import com.walker.core.encode.JsonFastUtil;
import com.walker.core.mode.Bean;
import com.walker.core.util.MapListUtil;
import com.walker.core.util.TimeUtil;
import com.walker.dao.ConfigDao;
import com.walker.dao.JdbcDao;
import com.walker.mapper.StatisticsMapper;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Tomcat监控后台
 * @author Walker
 *
 */
@Controller
@RequestMapping("/tomcat")
public class TomcatController {
    private Logger log = LoggerFactory.getLogger(getClass());

//    每条线名称
    List<String> lineNames = Arrays.asList("成功次数", "成功耗时", "失败次数", "失败耗时", "成功率");
//    每条线类型
    List<String> lineTypes = Arrays.asList("bar", "bar", "bar", "bar", "line");
//    每条线堆叠类型
    List<String> lineStacks = Arrays.asList("1", "2", "1", "2", "3");



    @Autowired
    ConfigDao configDao;
    @Autowired
    JdbcDao baseService;
    @Autowired
    StatisticsMapper statisticsMapper;

//
//    if (Tools.notNull(new Object[]{url}) && !url.toLowerCase().equals("undefined") && !url.toLowerCase().equals("null")) {
//        if (baseService.getDs().equals("oracle")) {
//            list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT LEV, NVL(TIME, '0') TIME FROM  ( SELECT HOUR, CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM (  SELECT  TO_CHAR(LT.TIME, 'HH24') HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  )GROUP BY HOUR   ) T1,  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <= 24    ) T2 WHERE T1.HOUR(+) = T2.LEV  ORDER BY LEV ", new Object[]{url}));
//        } else {
//            list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT T2.LEV, IFNULL(T1.TIME, '0') TIME FROM   ( SELECT HOUR, (0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM (  SELECT  SUBSTR(LT.TIME,12,2) HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  ) T GROUP BY HOUR   ) T1 RIGHT JOIN  ( select lpad(level, 2, '0') lev from (select  (@i/*'*/:=/*'*/@i+1) level from  information_schema.COLUMNS t ,(select   @i/*'*/:=/*'*/0) it ) t  where level<=24    ) T2 ON T1.HOUR=T2.LEV ORDER BY LEV\t", new Object[]{url}));
//        }
//    } else if (baseService.getDs().equals("oracle")) {
//        list = MapListUtil.toArrayAndTurn(baseService.find("SELECT URL,CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
//    } else {
////            list = MapListUtil.toArrayAndTurn(baseService.find("SELECT URL,(0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
//        listDb = statisticsMapper.findAction();
//        list = MapListUtil.toArrayAndTurn(listDb);
//    }

    @ApiOperation(value = "统计概览", notes = "")
    @ResponseBody
    @RequestMapping(value = "/statics.do", method = RequestMethod.GET)
    public Response statics(
            @RequestParam(value = "from", required = false, defaultValue = "") String from,
            @RequestParam(value = "to", required = false, defaultValue = "") String to
    ) {
//        确保时间区间7天
        long d = 7 * 8 * configDao.get("com.walker.controller.TomcatController.socket.d", 3600 * 1000 * 3L);

        if(from.length() == 0 && to.length() == 0){
            to = TimeUtil.getTime(TimeUtil.ymdhms, 0);
            from = TimeUtil.getTime(TimeUtil.ymdhms, -d);
        }else if(from.length() == 0){
            from = TimeUtil.getTime(to, TimeUtil.ymdhms, -d);
        }else if(to.length() == 0){
            to = TimeUtil.getTime(from, TimeUtil.ymdhms, +d);
        }

//        指标按行查询出来 首列为x轴坐标, 后续每列为每一条线 指标
        List<Map<String, Object>> listDb = statisticsMapper.findAction(from, to);

        Map option = MapListUtil.makeEchartOption("Action概览", "W_LOG_TIME", ""
                , listDb, lineNames, lineTypes, lineStacks);
        log.debug(JsonFastUtil.toString(option));

        Map res = MapListUtil.getMap()
                .put("res", "true")
                .put("option", option)
                .put("args", new Bean().set("from", from).set("to", to))
                .build();

        return Response.makeTrue("", res);
    }
    @ApiOperation(value = "统计详情", notes = "")
    @ResponseBody
    @RequestMapping(value = "/staticsDetail.do", method = RequestMethod.GET)
    public Response staticsDetail(
            @RequestParam(value = "url", required = false, defaultValue = "") String url,
            @RequestParam(value = "from", required = false, defaultValue = "") String from,
            @RequestParam(value = "to", required = false, defaultValue = "") String to
    ) {
//        确保时间区间1天
        long d = 8 * configDao.get("com.walker.controller.TomcatController.socket.d", 3600 * 1000 * 3L);
        if(from.length() == 0 && to.length() == 0){
            to = TimeUtil.getTime(TimeUtil.ymdhms, 0);
            from = TimeUtil.getTime(TimeUtil.ymdhms, -d);
        }else if(from.length() == 0){
            from = TimeUtil.getTime(to, TimeUtil.ymdhms, -d);
        }else if(to.length() == 0){
            to = TimeUtil.getTime(from, TimeUtil.ymdhms, +d);
        }

        List<Map<String, Object>> list = statisticsMapper.findActionUrl(from, to);
        List<Object> urls = MapListUtil.getListCol(list, 0);

//        指标按行查询出来 首行为x轴坐标
        List<Map<String, Object>> listDb = statisticsMapper.findActionDetail(from, to, url);
        Map option = MapListUtil.makeEchartOption("Action详情", "W_LOG_TIME", ""
                , listDb, lineNames, lineTypes, lineStacks);
        log.debug(JsonFastUtil.toString(option));
        Map res = MapListUtil.getMap()
                .put("res", "true")
                .put("option", option)
                .put("items", urls)
                .put("args", new Bean().set("from", from).set("to", to).set("url", url))
                .build();

        return Response.makeTrue("", res);
    }


    @ApiOperation(value = "每天新增用户", notes = "")
    @ResponseBody
    @RequestMapping(value = "/staticsUser.do", method = RequestMethod.GET)
    public Response staticsUser(
            @RequestParam(value = "from", required = false, defaultValue = "") String from,
            @RequestParam(value = "to", required = false, defaultValue = "") String to
    ) {
//        确保时间区间30天
        long d = 30 * configDao.get("com.walker.controller.TomcatController.socket.d", 3600 * 1000 * 3L);
        if(from.length() == 0 && to.length() == 0){
            to = TimeUtil.getTime(TimeUtil.ymdhms, 0);
            from = TimeUtil.getTime(TimeUtil.ymdhms, -d);
        }else if(from.length() == 0){
            from = TimeUtil.getTime(to, TimeUtil.ymdhms, -d);
        }else if(to.length() == 0){
            to = TimeUtil.getTime(from, TimeUtil.ymdhms, +d);
        }

        List<Map<String, Object>> listDb = statisticsMapper.findUserData(from, to);

        Map option = MapListUtil.makeEchartOption("数据变化", "W_USER/W_DEPT/W_ROLE/W_AREA", ""
                , listDb, Arrays.asList("新增用户", "修改用户", "新增部门", "修改部门", "新增角色", "修改角色", "新增地理", "修改地理"), null, null);
        log.debug(JsonFastUtil.toString(option));
        Map res = MapListUtil.getMap()
                .put("res", "true")
                .put("option", option)
                .put("args", new Bean().set("from", from).set("to", to))
                .build();

        return Response.makeTrue("", res);
    }




    @ApiOperation(value = "统计socket", notes = "")
    @ResponseBody
    @RequestMapping(value = "/socket.do", method = RequestMethod.GET)
    public Response socket(
            @RequestParam(value = "from", required = false, defaultValue = "") String from,
            @RequestParam(value = "to", required = false, defaultValue = "") String to,
            @RequestParam(value = "url", required = false, defaultValue = "") String url
    ) {
        //        确保时间区间1天
        long d = configDao.get("com.walker.controller.TomcatController.socket.d", 3600 * 1000 * 3L);
        if(from.length() == 0 && to.length() == 0){
            to = TimeUtil.getTime(TimeUtil.ymdhms, 0);
            from = TimeUtil.getTime(TimeUtil.ymdhms, -d);
        }else if(from.length() == 0){
            from = TimeUtil.getTime(to, TimeUtil.ymdhms, -d);
        }else if(to.length() == 0){
            to = TimeUtil.getTime(from, TimeUtil.ymdhms, +d);
        }

        List<Map<String, Object>> list = statisticsMapper.findSocketDetailUrl(from, to);
        List<Object> urls = MapListUtil.getListCol(list, 0);


        //    每条线名称
        List<String> lineNames = Arrays.asList("网络数", "网络平均耗时", "排队数", "排队平均耗时", "处理数", "处理平均耗时");
        //    每条线类型
        List<String> lineTypes = Arrays.asList("bar", "bar", "bar", "bar", "bar", "bar");
        //    每条线堆叠类型
        List<String> lineStacks = Arrays.asList("1", "2", "1", "2", "1", "2");

//        指标按行查询出来 首行为x轴坐标
        List<Map<String, Object>> listDb = statisticsMapper.findSocketDetail(from, to, url);
        Map option = MapListUtil.makeEchartOption("Socket趋势", "W_LOG_SOCKET_MODEL", ""
                , listDb, lineNames, lineTypes, lineStacks);
        log.debug(JsonFastUtil.toString(option));
        Map res = MapListUtil.getMap()
                .put("res", "true")
                .put("option", option)
                .put("items", urls)
                .put("args", new Bean().set("from", from).set("to", to).set("url", url))
                .build();

        return Response.makeTrue("", res);

    }

//+----------------------------------+-------------+-------------------------------------+--------------------+----------------+------------+----------+---------------+---------+---------+---------+------------+-------------+----------------+
//| id                               | invoke_date | service                             | method             | consumer       | provider   | type     | invoke_time   | success | failure | elapsed | concurrent | max_elapsed | max_concurrent |
//+----------------------------------+-------------+-------------------------------------+--------------------+----------------+------------+----------+---------------+---------+---------+---------+------------+-------------+----------------+
//| 36cd17a74b504665a50dfd7b8219b9ab | 2020-07-28  | com.walker.service.LogService       | saveLogModel       | 172.17.149.176 | 172.18.0.1 | provider | 1595901121739 |    2149 |       0 |    7842 |          1 |          31 |              3 |
//| 4bc23d7265f84428beebc18b5b8b58b2 | 2020-07-28  | com.walker.service.LogService       | saveLogSocketModel | 172.17.149.176 | 172.18.0.1 | provider | 1595901121739 |     997 |       0 |   33369 |          1 |         133 |              1 |
//| 9c76c0f73ddd430396d632e9dd214e9f | 2020-07-28  | com.walker.service.SyncService      | doAction           | 172.17.149.176 | 172.18.0.1 | provider | 1595901121739 |       1 |       0 |    3559 |          1 |        3559 |              1 |
//| b8b1579c96f245a8b73e5a7a4ef9b6f5 | 2020-07-28  | com.walker.service.RoleService      | finds              | 172.18.0.1     | 172.18.0.1 | consumer | 1595901121888 |       3 |       0 |     290 |          1 |         157 |              1 |
//| c020d00750494e70bef24bb2acf7de47 | 2020-07-28  | com.walker.service.SysConfigService | count              | 172.18.0.1     | 172.18.0.1 | consumer | 1595901121888 |     134 |       0 |    5159 |          1 |          91 |              1 |
//| f175e9805292439a92d670c68ac8ae57 | 2020-07-28  | com.walker.service.LogService       | saveLogModelNoTime | 172.17.149.176 | 172.18.0.1 | provider | 1595901121739 |    1068 |       0 |   40346 |          1 |         289 |              2 |
//+----------------------------------+-------------+-------------------------------------+--------------------+----------------+------------+----------+---------------+---------+---------+---------+------------+-------------+----------------+
    @ApiOperation(value = "统计dubbo monitor", notes = "")
    @ResponseBody
    @RequestMapping(value = "/dubbo.do", method = RequestMethod.GET)
    public Response dubbo(
            @RequestParam(value = "from", required = false, defaultValue = "") String from,
            @RequestParam(value = "to", required = false, defaultValue = "") String to,
            @RequestParam(value = "url", required = false, defaultValue = "") String url,   //service

            @RequestParam(value = "consumer", required = false, defaultValue = "") String consumer,
            @RequestParam(value = "provider", required = false, defaultValue = "") String provider,
            @RequestParam(value = "type", required = false, defaultValue = "") String type

            ) {
        //        确保时间区间1天
        long d = 4 * configDao.get("com.walker.controller.TomcatController.socket.d", 3600 * 1000 * 3L);
        if(from.length() == 0 && to.length() == 0){
            to = TimeUtil.getTime(TimeUtil.ymdhms, 0);
            from = TimeUtil.getTime(TimeUtil.ymdhms, -d);
        }else if(from.length() == 0){
            from = TimeUtil.getTime(to, TimeUtil.ymdhms, -d);
        }else if(to.length() == 0){
            to = TimeUtil.getTime(from, TimeUtil.ymdhms, +d);
        }

        List<Map<String, Object>> list = statisticsMapper.findDubboDetailUrl(from, to);
        List<Object> urls = MapListUtil.getListCol(list, 0);

//  | success | failure | elapsed | concurrent | max_elapsed | max_concurrent |
        //    每条线名称
        List<String> lineNames = Arrays.asList("成功次数", "失败次数", "耗时", "并发数", "最大耗时", "最大并发数");
        //    每条线类型
        List<String> lineTypes = Arrays.asList("bar", "bar", "bar", "line", "bar", "line");
        //    每条线堆叠类型
        List<String> lineStacks = Arrays.asList("1", "1", "2", "3", "4", "3");

//        指标按行查询出来 首行为x轴坐标
        List<Map<String, Object>> listDb = statisticsMapper.findDubboDetail(from, to, url, consumer, provider, type);
        Map option = MapListUtil.makeEchartOption("Dubbo业务趋势", "dubbo_invoke", ""
                , listDb, lineNames, lineTypes, lineStacks);
        log.debug(JsonFastUtil.toString(option));
        Map res = MapListUtil.getMap()
                .put("res", "true")
                .put("option", option)
                .put("items", urls)
                .put("args", new Bean().set("from", from).set("to", to).set("url", url).set("consumer", consumer).set("provider", provider).set("type", type))
                .build();

        return Response.makeTrue("", res);

    }

//option = {
//        title: {
//            text: '深圳月最低生活费组成（单位:元）',
//            subtext: 'From ExcelHome',
//            sublink: 'http://e.weibo.com/1341556070/AjQH99che'
//        },
//        legend: {
//            data: ['百度', '谷歌' ]
//        },
//        xAxis: [
//            {
//                data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
//            }
//        ],
//        yAxis: [
//            {  }
//        ],
//        series: [
//            {
//                name: '百度', type: 'bar', stack: '搜索引擎',
//                data: [620, 732, 701, 734, 1090, 1130, 1120]
//            },
//            {
//                name: '谷歌', type: 'bar', stack: '搜索引擎',
//                data: [120, 132, 101, 134, 290, 230, 220]
//            },
//        ]
//    };

}