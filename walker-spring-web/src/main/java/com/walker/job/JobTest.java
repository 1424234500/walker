package com.walker.job;

import com.walker.quartz.TaskJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobTest extends TaskJob {
	private Logger log = LoggerFactory.getLogger(getClass());


	@Override
	public String make() {
		log.info("begin---------");

		String res = "jobTest";
		log.info(res);

		log.info("end---------");
		return res;
	}
}
