package com.walker.job;

import com.walker.core.util.SpringContextUtil;
import com.walker.dao.ConfigDao;
import com.walker.quartz.TaskJob;
import com.walker.service.MakeTestService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 曲线控制数据模型
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobMakeLogSocketModel extends TaskJob {
	private Logger log = LoggerFactory.getLogger(getClass());

//	@Autowired
//	@Qualifier("syncService")
//	private SyncService syncService;
	//此处不能自动注入? 扫描注入包配置问题
	MakeTestService makeTestService = SpringContextUtil.getBean("makeTestService");
	ConfigDao configDao = SpringContextUtil.getBean("configDao");


	@Override
	public String make() {
		log.info("begin---------");

		String plugin = configDao.get("com.walker.job.JobMakeLogSocketModel.plugin", "test");
		int pCount = configDao.get("com.walker.job.JobMakeLogSocketModel.pcount", 100);
		int pCost = configDao.get("com.walker.job.JobMakeLogSocketModel.pcost", 100);
		Long t = configDao.get("com.walker.job.JobMakeLogSocketModel.t", 3600L);

		Object bean = makeTestService.makeLogOneLine(plugin, pCount, pCost, t);
		String res = String.valueOf(bean);
		log.info(res);

		log.info("end---------");
		return res;
	}

}
