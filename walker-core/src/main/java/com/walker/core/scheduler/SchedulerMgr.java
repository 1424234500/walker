package com.walker.core.scheduler;


import com.walker.core.aop.ConnectorAdapter;;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum Type {
    QUARTZ,
}

/**
 * 管理器
 */
public class SchedulerMgr extends ConnectorAdapter; {
    private static final Logger log = LoggerFactory.getLogger(SchedulerMgr.class);

    private static Scheduler scheduler = null;

    public SchedulerMgr() {
    }

    public static Scheduler getInstance() {
        if (scheduler == null) {
            scheduler = getInstance(Type.QUARTZ);
        }
        return scheduler;
    }

    public static Scheduler getInstance(Type type) {
        Scheduler scheduler = null;
        switch (type) {
            case QUARTZ:
                scheduler = new SchedulerQuartzImpl();
                break;
            default:
                scheduler = new SchedulerQuartzImpl();
        }
        return scheduler;
    }

    /**
     * 初始化scheduler 系统级数据 环境设置读取 词典加载 额外配置项
     * 1.加载配置文件
     * 2.加载数据库
     */
    public void reload(Scheduler scheduler) {
        log.warn("Reload scheduler from file / db");
        Task task = new Task("job_sys_test", "util.scheduler.job.JobTest", "quartz scheduler tools out");
        task.addCron("0/30 * * * * ?");
        try {
            scheduler.add(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean doInit() {
        return getInstance() != null;
    }

    public boolean doTest() {
        return getInstance() != null;
    }


}
