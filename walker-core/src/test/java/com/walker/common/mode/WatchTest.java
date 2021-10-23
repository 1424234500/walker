package com.walker.common.mode;

import com.walker.util.ThreadUtil;
import com.walker.util.Tools;
import com.walker.util.Watch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchTest {
	private static Logger log = LoggerFactory.getLogger(WatchTest.class);

	@Test
	public void testWatch2() {
		Watch w = new Watch("query");
		ThreadUtil.sleep(1000);
		w.cost();
		ThreadUtil.sleep(1000);
		try {
			w.exceptionWithThrow(new Exception("aaaaaaaaaaa"), log);
		}catch(Exception e) {
			
		}
		Tools.out(w);
	}
	@Test
	public void testWatch3() {
		Watch w = new Watch("query");
		ThreadUtil.sleep(1000);
		w.cost("sql");
		ThreadUtil.sleep(200);
		w.cost("parse");
		ThreadUtil.sleep(10);
		w.cost("other");
		ThreadUtil.sleep(50);
		w.cost("return");

		w.res(5, log);
		Tools.out(w);
		Tools.out(w.toPrettyString());
	}
	
	
}
