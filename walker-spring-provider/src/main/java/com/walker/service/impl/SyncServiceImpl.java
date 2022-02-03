package com.walker.service.impl;


import com.walker.config.MakeConfig;
import com.walker.core.encode.PinyinUtil;
import com.walker.mode.Page;
import com.walker.dao.BaseDateRepository;
import com.walker.dao.ConfigDao;
import com.walker.dao.JdbcTemplateDao;
import com.walker.dao.RedisDao;
import com.walker.mode.*;
import com.walker.service.*;
import com.walker.system.Pc;
import com.walker.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 调度器
 * <p>
 * 周期执行任务
 * sql操作
 * <p>
 * 初始化数据
 * <p>
 * 定期造数
 * 没
 * <p>
 * 同步数据
 */
@Service("syncService")
public class SyncServiceImpl implements SyncService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    SyncAreaService syncAreaService;


    @Autowired
    UserService userService;
    @Autowired
    DeptService deptService;
    @Autowired
    AreaService areaService;
    @Autowired
    LogService logService;

    @Autowired
    ActionService actionService;

    @Autowired
    BaseDateRepository baseDateRepository;

    @Autowired
    JdbcTemplateDao jdbcTemplateDao;
    @Autowired
    RedisDao redisDao;
    @Autowired
    MakeConfig makeConfig;
    @Autowired
    ConfigDao configDao;

    ExecutorService executorService = ThreadUtil.getExecutorServiceInstance(10);


    @Override
    public Bean doBaseData(Bean args) {
        Bean res = new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
        String key = "com.walker.service.impl.doBaseData";
        String value = redisDao.tryLock(key, configDao.get(key, 3600), 1);
        res.set("KEY", key);
        res.set("VALUE", value);
        if (value != null && value.length() > 0) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("sync begin key:" + key + " value:" + value + " args:" + args);
                    Watch watch = new Watch(key);
                    String format = TimeUtil.ymdh;
                    //保留100天
                    String day100 = TimeUtil.getTime(format, -100);
                    //任务名 类名 触发器名
                    LogModel logModel = LogModel.getDefaultModel()
                            .setCATE(Config.getCateJob())
                            .setUSER(Config.getSystemUser())
                            .setARGS(String.valueOf(args))
                            .setABOUT("制造日期序列数据")
                            .setRES(null);
                    try {

                        org.springframework.data.domain.Page<BaseDate> list = baseDateRepository.findsRecently(PageRequest.of(0, 1));
                        if (list.getContent().size() > 0) {
                            baseDateRepository.delete(day100);
                            String str = list.getContent().get(0).getS_MTIME();
                            if (str == null || str.length() == 0) {
                                log.error("base date no correct");
                                logModel.setABOUT("base date no correct" + list.getContent());
                            } else {
                                day100 = str;
                            }
                        }
                        watch.cost("finddb");
                        //构造时间序列 day100 -> tomorry
                        long tfrom = TimeUtil.format(day100, TimeUtil.ymdh);
                        long tto = System.currentTimeMillis() + 10 * TimeUtil.daymills;
                        log.info(TimeUtil.getTime(tfrom, TimeUtil.ymdhms) + " " + TimeUtil.getTime(tto, TimeUtil.ymdhms));
                        List<BaseDate> list1 = new ArrayList<>();
                        for (long i = tfrom; i < tto; i += 60 * 60 * 1000L) {//小时最细
                            list1.add(new BaseDate().setS_MTIME(TimeUtil.getTime(i, format)));
                        }
                        baseDateRepository.saveAll(list1);
                        watch.res(list1.size());
                        logModel.setIS_OK(Config.TRUE);
                    } catch (Exception e) {
                        watch.exception(e);
                        logModel.setEXCEPTION(Tools.toString(e)).setIS_OK(Config.FALSE);
                        log.error(watch.toString(), e);
                    } finally {
                        logModel.setRES(watch.toPrettyString());
                        log.info(watch.toPrettyString());
                        log.info("sync end key:" + key + " value:" + value + " args:" + args);

                        if (!redisDao.releaseLock(key, value)) {
                            log.error("unlock error expire ? key:" + key + ", value:" + redisDao.get(key) + " should be value:" + value);
                        }
                    }
                }
            });
            res.set("INFO", "ok, sync in thread ");
            log.info("have being running " + res + " args:" + args);
        } else {
            res.set("INFO", "waring, redis lock error, have being syncing ? ");
            log.warn("have being locked " + res + " args:" + args);
        }
        return res;
    }

    @Override
    public Bean doAction(Bean args) {

        Bean res = new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
        String key = "com.walker.service.impl.doAction";
        String value = redisDao.tryLock(key, configDao.get(key, 36000), 1);
        res.set("KEY", key);
        res.set("VALUE", value);
        if (value != null && value.length() > 0) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("sync begin key:" + key + " value:" + value + " args:" + args);
                    Watch watch = new Watch(key);
                    //任务名 类名 触发器名
                    LogModel logModel = new LogModel()
                            .setID(LangUtil.getGenerateId())
                            .setCATE(Config.getCateJob())
                            .setUSER(Config.getSystemUser())
                            .setURL(getClass().getName())
                            .setIP_PORT_FROM(Pc.getIps().toString())
                            .setIP_PORT_TO(Pc.getIps().toString())
                            .setARGS(String.valueOf(args))
                            .setIS_EXCEPTION(Config.FALSE)
                            .setABOUT("任务队列执行开始")
                            .setRES(null);
                    logModel = logService.saveLogModelNoTime(logModel);

                    try {

                        List<Action> list = actionService.finds(
                                new Action().setS_FLAG(Config.TRUE), //异常jpa?????????
                                new Page()
                                        .setNowpage(1)
                                        .setShownum(Config.getDbsize())
                                        .setOrder("S_MTIME")
                        );

                        watch.cost("finddb");

                        for (int i = 0; i < list.size(); i++) {
                            Action action = list.get(i);
                            try {
                                watch.put("type", action.getTYPE()).put("value", action.getVALUE());
                                Object res = null;
                                if (action.getTYPE().equalsIgnoreCase("sql")) {
                                    if (action.getVALUE().endsWith(";")) {
                                        action.setVALUE(action.getVALUE().substring(0, action.getVALUE().length() - 1));
                                    }
                                    res = jdbcTemplateDao.executeSql(action.getVALUE());
                                } else if (action.getTYPE().equalsIgnoreCase("url")) {
                                    res = new HttpBuilder(action.getVALUE(), HttpBuilder.Type.GET).buildString();
                                } else if (action.getTYPE().equalsIgnoreCase("class")) {
                                    res = ClassUtil.doPackage(action.getVALUE(), new ArrayList<>());
                                }
                                watch.put("res", res);
                                log.info(watch.toString());
                            } catch (Exception e) {
                                watch.put(e.getMessage());
                                log.error(watch.toString(), e);
                            } finally {
                                watch.cost(action.getNAME());
                            }
                        }
                        logModel.setIS_OK(Config.TRUE);
                    } catch (Exception e) {
                        watch.exception(e);
                        logModel.setEXCEPTION(Tools.toString(e)).setIS_OK(Config.FALSE);
                        log.error(watch.toString(), e);
                    } finally {
                        watch.res();
                        logModel.setRES(watch.toPrettyString());
                        logModel.setABOUT("任务执行完毕");
                        log.info(watch.toPrettyString());
                        log.info("sync end key:" + key + " value:" + value + " args:" + args);

                        if (!redisDao.releaseLock(key, value)) {
                            log.error("unlock error expire ? key:" + key + ", value:" + redisDao.get(key) + " should be value:" + value);
                        }
                        logService.saveLogModelNoTime(logModel);
                    }
                }
            });
            res.set("INFO", "ok, sync in thread ");
            log.info("have being running " + res + " args:" + args);
        } else {
            res.set("INFO", "waring, redis lock error, have being syncing ? ");
            log.warn("have being locked " + res + " args:" + args);
        }
        return res;

    }

    @Override
    public Bean syncArea(Bean args) {
        Bean res = new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
        String key = "com.walker.service.impl.syncArea";
        String value = redisDao.tryLock(key, configDao.get(key, 20 * 3600), 1);
        res.set("KEY", key);
        res.set("VALUE", value);
        if (value != null && value.length() > 0) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("sync begin key:" + key + " value:" + value + " args:" + args);
                    try {
                        syncAreaService.syncArea();
                    } finally {
                        log.info("sync end key:" + key + " value:" + value + " args:" + args);
                        if (!redisDao.releaseLock(key, value)) {
                            log.error("unlock error expire ? key:" + key + ", value:" + redisDao.get(key) + " should be value:" + value);
                        }
                    }
                }
            });
            res.set("INFO", "ok, sync in thread ");
            log.info("have being running " + res + " args:" + args);
        } else {
            res.set("INFO", "waring, redis lock error, have being syncing ? ");
            log.warn("have being locked " + res + " args:" + args);
        }
        return res;
    }

    @Override
    public Bean syncDept(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }

    @Override
    public Bean syncUser(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }

    /**
     * 造数
     *
     * @param args
     */
    @Override
    public Bean makeUser(Bean args) {
        Bean res = new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
        String key = "com.walker.service.impl.makeUser";
        String value = redisDao.tryLock(key, configDao.get(key, 10 * 60), 1);
        res.set("KEY", key);
        res.set("VALUE", value);
        if (value != null && value.length() > 0) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("sync begin key:" + key + " value:" + value + " args:" + args);

                    Watch watch = new Watch(key);

                    try {
                        /**
                         * 造用户数据
                         * 5线程 共计造1000用户
                         */

                        int countMakeUser = configDao.get("com.walker.service.impl.makeUser.count", 10);
                        List<User> list = new ArrayList<>();
                        long day = Integer.valueOf(TimeUtil.getTime(TimeUtil.ymd1)) * countMakeUser;//20200215
                        String time = TimeUtil.getTimeYmdHms();

                        Dept dept = new Dept().setS_FLAG(Config.TRUE);
                        int needDeptSize = 10;
                        int deptSize = deptService.count(dept);
                        int pageSize = (int) Math.ceil(1D * deptSize / needDeptSize);
                        List<Dept> listDepts = deptService.finds(
                                dept, new Page().setShownum(needDeptSize).setPagenum((int) (Math.random() * pageSize))
                        );
                        listDepts = listDepts == null || listDepts.size() == 0 ? Arrays.asList(new Dept().setID("0000000")) : listDepts;
                        watch.cost("findDept", listDepts.size());

                        Area area = new Area().setS_FLAG(Config.TRUE);
                        int needAreaSize = 10;
                        int areaSize = areaService.count(area);
                        int pageSizeArea = (int) Math.ceil(1D * areaSize / needAreaSize);
                        List<Area> listAreas = areaService.finds(
                                area, new Page().setShownum(needAreaSize).setPagenum((int) (Math.random() * pageSizeArea))
                        );
                        listAreas = listAreas == null || listAreas.size() == 0 ? Arrays.asList(new Area().setID("0000000")) : listAreas;
                        watch.cost("findArea", listAreas.size());

                        for (int i = 0; i < countMakeUser; i++) {
                            String id = (day + i) + "";
                            String deptId = listDepts.get((int) (Math.random() * listDepts.size())).getID();
                            String areaId = listAreas.get((int) (Math.random() * listAreas.size())).getID();
                            String name = PinyinUtil.getRandomName(1, null);

                            User user = new User()
                                    .setID(id)
                                    .setDEPT_ID(deptId)
                                    .setAREA_ID(areaId)
                                    .setNAME(name)
                                    .setSIGN(name + "'s " + PinyinUtil.getRandomName(10, 1, null))
                                    .setS_ATIME(time)
                                    .setS_MTIME(time)
                                    .setS_FLAG(Config.TRUE)
                                    .setSEX(Math.random() * 10 > 5 ? Config.TRUE : Config.FALSE);
                            list.add(user);
                            if (list.size() > 500 - 1) {
                                userService.saveAll(list);
                                list.clear();
                            }

                        }
                        if (list.size() > 0) {
                            userService.saveAll(list);
                            list.clear();
                        }
                        watch.cost("makeData", countMakeUser);


                    } finally {
                        watch.res();
                        log.info(watch.toPrettyString());
                        log.info("sync end key:" + key + " value:" + value + " args:" + args);

                        if (!redisDao.releaseLock(key, value)) {
                            log.error("unlock error expire ? key:" + key + ", value:" + redisDao.get(key) + " should be value:" + value);
                        }
                    }
                }
            });
            res.set("INFO", "ok, sync in thread ");
            log.info("have being running " + res + " args:" + args);
        } else {
            res.set("INFO", "waring, redis lock error, have being syncing ? ");
            log.warn("have being locked " + res + " args:" + args);
        }
        return res;
    }

    @Override
    public Bean makeDept(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }

    @Override
    public Bean makeRole(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }


}
