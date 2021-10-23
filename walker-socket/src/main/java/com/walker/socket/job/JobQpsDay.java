package com.walker.socket.job;

import com.walker.core.scheduler.TaskJob;
import com.walker.util.Tools;

public class JobQpsDay extends TaskJob {


	@Override
	public String make() {
		Tools.out("scheduler quartz run test");
		return "world";
	}
}
