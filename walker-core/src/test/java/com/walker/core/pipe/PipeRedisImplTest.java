package com.walker.core.pipe;

import com.walker.core.aop.Fun;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PipeRedisImplTest {

	@Test
	public void testStart() {
		Pipe<String> pipe = new PipeRedisImpl();
		pipe.start("key");
		
		assertTrue(pipe.put("a"));
		assertEquals("a", pipe.get());
		assertTrue(pipe.putHead("2"));
		assertTrue(pipe.putHead("1"));
		assertEquals("1", pipe.get());
		assertEquals("2", pipe.get());
		
		assertTrue(pipe.put(Arrays.asList("b", "c")));
		pipe.startConsumer(2, new Fun<String>() {
			@Override
			public void make(String obj) {
				assertTrue("b".equals(obj) || "c".equals(obj)) ;
			}
		});
		
		pipe.stopConsumer();
		pipe.stop();
	} 

}
