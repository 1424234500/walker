package com.walker.spring.service.impl;


import com.walker.core.mode.Key;
import com.walker.core.mode.school.Area;
import com.walker.core.util.*;
import com.walker.service.AreaService;
import com.walker.service.Config;
import com.walker.service.SyncAreaService;
import com.walker.spring.component.RedisLock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 初始化数据服务
 */
@Service("syncAreaService")
public class SyncAreaServiceImpl implements SyncAreaService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    RedisLock redisLock;

    @Autowired
    AreaService areaService;

    /**
     * 国家统计局 地区分级 遍历抓取数据
     * 耗时 中断恢复进度问题？
     * 使用redis存储已经ok的url，下次遇到则跳过， 仅重复上次的最后一个省级别或以下的最小单元
     */
    public void syncArea() {
        log.info("sync area begin thread");
//        Area root = getCityRoot();
        //此处按照一级省 分别递归获取树
        Area root = getCityRootChina();
        getCity(root, false, null);
        areaService.saveAll(Arrays.asList(root));
        Watch watchAll = new Watch("syncArea");
        for (int i = 0; i < root.getChilds().size() && i < 998; i++) {
            Area item = root.getChilds().get(i);

            //单省作为线程  等待执行完毕 阻塞操作
//            ThreadUtil.getExecutorServiceInstance(5).execute(new Runnable() {
//                @Override
//                public void run() {
            Watch watch = new Watch("area.sync." + item.getNAME());
            getCity(item, true, null);  //html获取构建省 树
            watch.cost("http");

            List<Area> list = tree2list(item, root.getID(), root.getPATH(), root.getPATH_NAME());   //递归构建树
            watch.put(list.size());
            watch.cost("tree");


            for (int ii = 0; ii < list.size(); ii += Config.getDbsize()) {
                List<Area> ll = list.subList(ii, Math.min(ii + Config.getDbsize(), list.size()));
//                        Tools.formatOut(ll);
                log.info("batch save " + ii + " of " + list.size());
                areaService.saveAll(ll);
            }
            watch.put("batch", list.size() / Config.getDbsize());
            watch.cost("db");
            log.info(watch.res().toPrettyString());

            watchAll.cost(item.getNAME());

//                }
//            });
        }

        log.info(watchAll.res().toPrettyString());
        log.info("sync area end");

    }


    /**
     * 递归树 构建每个父子关系并 便利出list方便存储
     *
     * @param area
     * @param pid
     * @param idPath
     * @param namePath
     */
    public List<Area> tree2list(Area area, String pid, String idPath, String namePath) {
        List<Area> res = new ArrayList<>();
        idPath = idPath + "/" + area.getID();
        namePath = namePath + "/" + area.getNAME();

        area.setP_ID(pid);
        area.setPATH(idPath);
        area.setPATH_NAME(namePath);
        area.setS_MTIME(TimeUtil.getTimeYmdHmss());
        area.setS_FLAG("1");
        res.add(area);

        for (Area ci : area.getChilds()) {
            res.addAll(tree2list(ci, area.getID(), idPath, namePath));
        }

        return res;
    }


    /**
     * 获取china root
     */
    public Area getCityRootChina() {
        Area root = new Area().setUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html");
        root.setID("0").setS_FLAG("1").setCODE("").setNAME("China").setLEVEL("" + 0).setPATH("/" + root.getID()).setPATH_NAME("/" + root.getNAME());
        return root;
    }

    /**
     * 遍历节点 字节点 生成树 或 获取list
     * 构建树时 判断url是否已经访问过 若是则不再重复 过期时间
     *
     * @param parent  root节点
     * @param ifChild 是否递归子节点
     * @param list    若不为null 则把节点都添加进去   大量数据不应当使用
     * @return int 返回异常次数
     */
    public int getCity(Area parent, boolean ifChild, List<Area> list) {
        int res = 0;
        try {
//            http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html
//            http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/51.html

            if (ifChild && isExists(parent)) { //若要遍历子节点 且 已经完成过了  不再遍历！！！ 例外了root 省级别
                log.warn("have done the node " + parent);
                return res;
            }

            String html1 = "";

            synchronized (this) {
                html1 = new HttpBuilder(parent.getUrl() + "?_t=" + System.currentTimeMillis(), HttpBuilder.Type.GET)
                        .setConnectTimeout(3000).setRequestTimeout(5000).setSocketTimeout(5000)
                        .setEncode("utf-8").setDecode("gbk").buildString();
                Thread.sleep(10);   //休眠间隔避免 高频率导致封ip？
            }
/*
<tr class="citytr">
    <td><a href="51/5101.html">510100000000</a></td><td><a href="51/5101.html">成都市</a></td>
</tr>
*/
//            Tools.out(html1);
            Document doc = Jsoup.parse(html1);
            Elements elements = null;
            String[] cs = {"provincetr", "citytr", "countytr", "towntr", "villagetr"};
            String type = "";
            for (int i = 0; i < cs.length; i++) {
                type = cs[i];
                elements = doc.select("tr[class=" + type + "]");//.select("a");
                if (elements != null && elements.size() > 0) break;
            }
            int newLevel = Integer.valueOf(parent.getLEVEL()) + 1;
            String urlBase = FileUtil.getFilePath(parent.getUrl());

            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String url = "", code = "", code1 = "", name = "";

                if (type.equalsIgnoreCase(cs[0])) {  //省 特殊
                    Elements elementCols = element.select("td").select("a");
                    for (int j = 0; j < elementCols.size(); j++) {
                        url = "";
                        Element elementCol = elementCols.get(j);
                        url = elementCol.attr("href");
                        if (url != null && url.length() > 0) {
                            url = urlBase + "/" + url;
                        }
                        code = FileUtil.getFileNameOnly(url);
                        name = elementCol.text();

                        Area area = new Area().setUrl(url);
                        area.setID(code).setNAME(name).setCODE(code1).setLEVEL("" + newLevel);
//                        Tools.out(newLevel, url, name, code, code1);
                        // 深度构建树  或者 广度构建    由上而下  由下而上 ？
                        parent.addChilds(Arrays.asList(area));
                        if (list != null) {
                            list.add(area);
                        }
                        if (url != null && url.length() > 0 && ifChild) {
                            res += getCity(area, ifChild, list);
                        }
                    }
                } else {
                    if (type.equalsIgnoreCase(cs[4])) { //镇 特殊
                        Elements elementLineV = element.select("td");
                        if (elementLineV.size() >= 3) {
                            code = elementLineV.get(0).text();
                            code1 = elementLineV.get(1).text();
                            name = elementLineV.get(2).text();
                        }
                    } else {
//<tr class="countytr"><td><a href="01/130105.html">130105000000</a></td><td><a href="01/130105.html">新华区</a></td></tr>
                        Elements elementLine = element.select("td").select("a");
                        url = "";
                        if (elementLine.size() > 0) {
                            url = elementLine.get(0).attr("href");
                        } else {
                            elementLine = element.select("td");
                        }
                        if (url != null && url.length() > 0) {
                            url = urlBase + "/" + url;
                        }
                        code = elementLine.get(0).text();
                        name = elementLine.get(1).text();
                    }
                    Area area = new Area().setUrl(url);
                    area.setID(code).setNAME(name).setCODE(code1).setLEVEL("" + newLevel);
                    Tools.out(newLevel, url, name, code, code1);

                    //                深度构建树  或者 广度构建    由上而下  由下而上 ？
                    parent.addChilds(Arrays.asList(area));
                    if (list != null) {
                        list.add(area);
                    }
                    if (url != null && url.length() > 0 && ifChild) {
                        res += getCity(area, ifChild, list);
                    }
                }
            }

            //若顺利获取完毕 构建完毕 树 但还没存储 就认为已经存过 有异常数则不记录成功， 子节点成功会记录 避免反复
            if (res == 0) {
                setDone(parent);
            }
        } catch (Exception e) {
            log.error(parent.toString(), e);
            res++;
        }

        return res;

    }


    public boolean isExists(Area area) {
        boolean res = false;

        String key = Key.getUrlDone();
        return redisLock.isLocked(key);
    }

    @Override
    public boolean setDone(Area area) {
        return false;
    }

}
