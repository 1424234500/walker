package com.walker.job;

import com.walker.quartz.TaskJob;
import com.walker.service.SyncService;
import com.walker.mode.Bean;
import com.walker.util.SpringContextUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步 地域信息
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobMakeRole extends TaskJob {
	private Logger log = LoggerFactory.getLogger(getClass());

//	@Autowired
//	@Qualifier("syncService")
//	private SyncService syncService;
	//此处不能自动注入? 扫描注入包配置问题
	SyncService syncService = SpringContextUtil.getBean("syncService");

	@Override
	public String make() {
		log.info("begin---------");

		Bean bean = syncService.makeRole(new Bean().set("hello", "world"));
		String res = String.valueOf(bean);
		log.info(res);

		log.info("end---------");
		return res;
	}

}
