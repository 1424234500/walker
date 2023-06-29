package com.walker.core.scheduler.job;

import com.walker.core.scheduler.TaskJob;
import com.walker.core.util.Tools;

public class JobTest extends TaskJob {

	@Override
	public String make() {
		Tools.out("scheduler quartz run test");

		return "world";
	}
}
