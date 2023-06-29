package com.walker.common.util;

import com.walker.core.Context;
import com.walker.core.util.Tools;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class ContextTest {

	@Test
	public void test() {
		URL url = Context.class.getResource("/");///walker/walker-socket/target/classes/
		File f = new File(url.getFile());
		Tools.out(f.getAbsolutePath());
		f = new File(f.getParent());
		Tools.out(f.getAbsolutePath());
		f = new File(f.getParent());
		Tools.out(f.getAbsolutePath());
		
		
		
		
		Tools.out(Context.getPathConf());
		Tools.formatOut(new File(Context.getPathConf()).list());
		
		
	}

}
