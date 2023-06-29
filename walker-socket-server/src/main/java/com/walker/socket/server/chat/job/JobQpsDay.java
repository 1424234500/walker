package com.walker.socket.server.chat.job;

import com.walker.core.scheduler.TaskJob;
import com.walker.core.util.Tools;

public class JobQpsDay extends TaskJob {


	@Override
	public String make() {
		Tools.out("scheduler quartz run test");
		return "world";
	}
}
