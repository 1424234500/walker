package com.walker.controller;


import com.walker.Response;
import com.walker.core.database.SqlUtil;
import com.walker.mode.Page;
import com.walker.core.scheduler.Task;
import com.walker.dao.JdbcDao;
import com.walker.service.BaseService;
import com.walker.service.CacheService;
import com.walker.service.ScheduleService;
import com.walker.util.LangUtil;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 任务调度配置
 */
@Api(value = "quartz工具")
@Controller
@RequestMapping("/quartz")
public class QuartzController {
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
    @Qualifier("scheduleService")
    private ScheduleService scheduleService;

    @ApiOperation(value = "立即执行任务", notes = "now")
    @ResponseBody
    @RequestMapping(value = "/run.do", method = RequestMethod.GET)
    public Response run(
            @RequestParam(value = "JOB_NAME", required = true, defaultValue = "") String jobName,
            @RequestParam(value = "JOB_CLASS_NAME", required = true, defaultValue = "") String jobClassName,
            @RequestParam(value = "DESCRIPTION", required = false, defaultValue = "") String description
    ) throws Exception {
        if(jobName.length() == 0){
            jobName = LangUtil.getGenerateId();
        }
        Task task = null;
        task = new Task(jobName, jobClassName, description);
        task = scheduleService.run(task);
        return Response.makeTrue("", task);
    }


    @ApiOperation(value = "保存任务", notes = "通用存储")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "JOB_NAME", required = true, defaultValue = "") String jobName,
            @RequestParam(value = "JOB_CLASS_NAME", required = true, defaultValue = "") String jobClassName,
            @RequestParam(value = "DESCRIPTION", required = false, defaultValue = "") String description
//            @RequestParam(value = "CRON_EXPRESSION", required = false, defaultValue = "") String cronExpression

    ) throws Exception {
        if(jobName.length() == 0){
            jobName = LangUtil.getGenerateId();
        }
        Task task = null;
//        if(cronExpression.length() > 0){
//            task = new Task(jobName, description, cronExpression);
//        }else{
            task = new Task(jobName, jobClassName, description);
//        }
        task = scheduleService.save(task);
        return Response.makeTrue("", task);
    }
    @ApiOperation(value = "保存触发器 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/saveTriggers.do", method = RequestMethod.GET)
    public Response saveTriggers(
            @RequestParam(value = "JOB_NAME", required = true, defaultValue = "") String jobName,
            @RequestParam(value = "JOB_CLASS_NAME", required = true, defaultValue = "") String jobClassName,
            @RequestParam(value = "ON", required = false, defaultValue = "") String on,
            @RequestParam(value = "OFF", required = false, defaultValue = "") String off
    ) throws Exception {
        String info = "JOB_NAME:" + jobName + " ON:" + on + " OFF:" + off;
        List<String> listOn = on.length() > 0 ? Arrays.asList(on.split(",")) : new ArrayList<>();
        List<String> listOff = off.length() > 0 ? Arrays.asList(off.split(",")) : new ArrayList<>();
        listOn = new ArrayList<>(new HashSet<>(listOn));
        listOff = new ArrayList<>(new HashSet<>(listOff));

        if(listOn.size() == 0 && listOff.size() == 0){
            return Response.makeFalse(info + " on off is null ");
        }
        if(jobName.length() == 0){
            return Response.makeFalse(info + " job is null ");
        }
        Task task = new Task().setId(jobName).setClassName(jobClassName);
        scheduleService.saveTrigger(task, listOn, listOff);

        return Response.makeTrue(info, task);
    }

    @ApiOperation(value = "删除任务", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "JOB_NAME", required = true, defaultValue = "") String jobName
    ) throws Exception {
        if(jobName.length() == 0){
            return Response.makeFalse("null ? args");
        }
        String jobNames[] = jobName.split(",");
        List<Task> res = new ArrayList<>();
        for(String clz : jobNames) {
            Task task = new Task(jobName);
            task = scheduleService.remove(task);
            res.add(task);
        }
        return Response.makeFalse(res.size() + "", res);
    }
    @ApiOperation(value = "分页查询任务列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "JOB_NAME", required = false, defaultValue = "") String jobName,
            @RequestParam(value = "JOB_CLASS_NAME", required = false, defaultValue = "") String jobClassName,
            @RequestParam(value = "DESCRIPTION", required = false, defaultValue = "") String description,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        StringBuilder sb = new StringBuilder("select * from  W_QRTZ_JOB_DETAILS where 1=1 ");
        List<Object> args = new ArrayList<>();
        if(jobName != null && jobName.length() > 0) {
            sb.append("and JOB_NAME like ? ");
            args.add("%" + jobName  + "%");
        }
        if(jobName != null && jobClassName.length() > 0) {
            sb.append("and JOB_CLASS_NAME like ? ");
            args.add("%" + jobClassName  + "%");
        }
        if(description != null && description.length() > 0) {
            sb.append("and DESCRIPTION like ? ");
            args.add("%" + description  + "%");
        }

        log.info("make sql " + sb.toString());
        List<?> res = jdbcDao.findPage(page, sb.toString(), args.toArray());
        return Response.makePage(SqlUtil.makeSql(sb.toString(), args.toArray()), page, res);
    }


    @ApiOperation(value = "分页查询 任务触发器", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPageTrigger.do", method = RequestMethod.GET)
    public Response findPageTrigger(
            @RequestParam(value = "JOB_NAME", required = true, defaultValue = "") String jobName,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        StringBuilder sb = new StringBuilder(
                "select j.JOB_NAME,c.CRON_EXPRESSION,t.TRIGGER_STATE,t.DESCRIPTION from  W_QRTZ_JOB_DETAILS j, W_QRTZ_TRIGGERS t, W_QRTZ_CRON_TRIGGERS c  where 1=1 and j.JOB_NAME=t.JOB_NAME and t.TRIGGER_NAME=c.TRIGGER_NAME and j.JOB_NAME=? " );

        log.info("make sql " + sb.toString());
        List<?> res = jdbcDao.findPage(page, sb.toString(), jobName);
        return Response.makePage(SqlUtil.makeSql(sb.toString(), jobName), page, res);
    }

    @ApiOperation(value = "分页查询 任务执行日志", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPageJobHis.do", method = RequestMethod.GET)
    public Response findPageJobHis(
            @RequestParam(value = "JOB_NAME", required = true, defaultValue = "") String jobName,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "10") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        StringBuilder sb = new StringBuilder(
                "select h.* from  W_QRTZ_JOB_DETAILS j, W_JOB_HIS h  where 1=1 and j.JOB_NAME=? and j.JOB_NAME=h.INFO order by h.S_TIME_START desc " );

        log.info("make sql " + sb.toString());
        List<?> res = jdbcDao.findPage(page, sb.toString(), jobName);
        return Response.makePage(SqlUtil.makeSql(sb.toString(), jobName), page, res);
    }


}
