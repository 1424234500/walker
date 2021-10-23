package com.walker.common.util;

import com.walker.util.TimeUtil;
import com.walker.util.Tools;
import org.junit.Test;

public class TimeUtilTest {

	@Test
	public void testFormatAuto() {
		String format = "yyyy-MM-dd HH:mm:ss";

		String from = TimeUtil.getTime(format, 0);

		long mindeta = -1L * 10 * 1000;
		for(int i = 0; i < 6; i++) {
			mindeta *= 10;
			
			String to = TimeUtil.getTime(format, mindeta);

			long max = TimeUtil.format(from, format);
			long min = TimeUtil.format(to, format);
			int deta = (int) (max - min);
			
			Tools.out(from, to, deta, TimeUtil.formatAuto(System.currentTimeMillis(), 0));
			
		}
		
	}

}
