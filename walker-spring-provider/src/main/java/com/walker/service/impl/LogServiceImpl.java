package com.walker.service.impl;


import com.walker.config.Config;
import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import com.walker.mode.Page;
import com.walker.dao.JdbcTemplateDao;
import com.walker.dao.LogModelRepository;
import com.walker.dao.LogSocketModelRepository;
import com.walker.dao.LogTimeRepository;
import com.walker.mode.sys.LogModel;
import com.walker.mode.sys.LogSocketModel;
import com.walker.mode.sys.LogTime;
import com.walker.service.LogService;
import com.walker.system.Pc;
import com.walker.mode.Bean;
import com.walker.util.LangUtil;
import com.walker.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transactional
@Service("logService")
//@Scope("prototype")	//默认单例 dubbo不能单例
public class LogServiceImpl implements LogService {
    private final Cache<String> cache = ConfigMgr.getInstance();
    @Autowired
    LogModelRepository logModelRepository;
    @Autowired
    LogSocketModelRepository logSocketModelRepository;
    @Autowired
    private JdbcTemplateDao jdbcTemplateDao;
    @Autowired
    private LogTimeRepository logTimeRepository;

    @Override
    public LogModel saveLogModelNoTime(LogModel logModel) {
        logModel.make();
        logModelRepository.save(logModel);
        return logModel;
    }

    /**
     * 需注意引用传递在dubbo中失效
     *
     * @param logModel
     */
    @Override
    public LogModel saveLogModel(LogModel logModel) {
        List<LogModel> list = cache.get(CACHE_KEY_CONTROL, new ArrayList<>());
        logModel.make();
        list.add(logModel);
        cache.put(CACHE_KEY_CONTROL, list);

        String url = logModel.getURL();
        url = url.split("\\?")[0]; //url编码
        Bean bean = cache.get(CACHE_KEY, new Bean());    //url : obj

        LogTime logTime = bean.get(url, null);
        if (logTime == null) {
            logTime = new LogTime()
                    .setIP_PORT(Pc.getIps().toString())
                    .setS_MTIME(TimeUtil.getTimeYmdHmss())
                    .setID(LangUtil.getGenerateId())
                    .setURL(url);
        }
        if (logModel.getIS_OK().equalsIgnoreCase(Config.TRUE)) {
            int count = logTime.getCOUNT_OK();
            float ave = logTime.getAVE_COST_OK();
            logTime.setAVE_COST_OK(1f * (ave * count + logModel.getCOST()) / (count + 1));
            logTime.setCOUNT_OK(count + 1);
        } else {
            int count = logTime.getCOUNT_NO();
            float ave = logTime.getAVE_COST_NO();
            logTime.setAVE_COST_NO(1f * (ave * count + logModel.getCOST()) / (count + 1));
            logTime.setCOUNT_NO(count + 1);
        }
        bean.put(url, logTime);
        cache.put(CACHE_KEY, bean);
        return logModel;
    }

    @Override
    public void saveStatis() {
        log.info("begin batch save");

        try {
            Bean bean = cache.get(CACHE_KEY, new Bean());
            cache.remove(CACHE_KEY);

            Set<Object> keys = bean.keySet();
            if (keys != null && keys.size() > 0) {
                List<LogTime> list = new ArrayList<>();
                for (Object key : keys) {
                    LogTime logTime = (LogTime) bean.get(key);
                    if (logTime != null)
                        list.add(logTime);
                }
                Object pages = Page.batch(list, Config.getDbsize(), new Page.FunArgsReturn<List<LogTime>, Integer>() {
                    @Override
                    public Integer make(List<LogTime> obj, Integer tbj) {
                        return logTimeRepository.saveAll(obj).size();
                    }
                });
                log.info("batch save static " + CACHE_KEY + " " + list.size() + " pages:" + pages);
            }
        } catch (Exception e) {
            log.error("saveStatis error " + CACHE_KEY, e);
        }

        try {
            List<LogModel> list = cache.get(CACHE_KEY_CONTROL, new ArrayList<>());
            cache.remove(CACHE_KEY_CONTROL);

            if (list.size() > 0) {
                Object pages = Page.batch(list, Config.getDbsize(), new Page.FunArgsReturn<List<LogModel>, Integer>() {
                    @Override
                    public Integer make(List<LogModel> obj, Integer tbj) {
                        return logModelRepository.saveAll(obj).size();
                    }
                });
                log.info("batch save static " + CACHE_KEY_CONTROL + " " + list.size() + " pages:" + pages);
            }
        } catch (Exception e) {
            log.error("saveStatis error " + CACHE_KEY_CONTROL, e);
        }

    }

    /**
     * 记录socket日志
     */
    @Override
    public LogSocketModel saveLogSocketModel(LogSocketModel logModel) {
        if (logModel.getID() == null || logModel.getID().length() == 0) {
            logModel.setID(LangUtil.getTimeSeqId());
        }
        if (logModel.getS_MTIME() == null || logModel.getS_MTIME().length() == 0) {
            logModel.setS_MTIME(TimeUtil.getTimeYmdHmss());
        }
        return logSocketModelRepository.save(logModel);
    }

}