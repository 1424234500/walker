package com.walker.core;

import com.walker.util.FileUtil;
import com.walker.util.Tools;

import java.io.File;
import java.nio.ByteBuffer;

public class Test {

	public static void main(String[] args) throws Exception {
//		testgc();
		FileUtil.showDir("D:\\Pictures\\pc\\BelgiumTSC_Training", new FileUtil.FunArgsReturnBool<File>() {
			@Override
			public Boolean make(File obj) {
				if(obj.isFile() && ( obj.getAbsolutePath().endsWith("csv")
				|| obj.getAbsolutePath().endsWith("txt"))
				){
					obj.delete();
					Tools.out(obj.getAbsoluteFile());
				}
				return true;
			}
		});

//		new TestModel();
	}
	public static void testgc()throws Exception{
		int d = 1024 * 1024 * 1;
		long all = 0;

		while(true) {
			all ++;
			Tools.out(all, "M");
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(d);
			Thread.sleep(10);
		}
	}

	public Test() throws InterruptedException {
		for(int i = 0; i < 4; i++) {
			new Thread() {
				public void run() {
					int j = 0;
					while (true) {
						System.out.println(j++);
					}

				}
			}.start();
		}
		Thread.sleep(999999999999L);

	}


}
