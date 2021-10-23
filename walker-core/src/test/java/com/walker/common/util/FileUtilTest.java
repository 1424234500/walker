package com.walker.common.util;

import com.walker.util.FileUtil;
import com.walker.util.Tools;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtilTest {



	@Test
	public void test() throws IOException {
//		3459791362
		Tools.out("crc", FileUtil.checksumCrc32(new File("/home/walker/e/help_note/shell/test.sh")));
	}
		
	List<String> paths = Arrays.asList(
			"/home/walker/test/web.log"
			,"/home/walker/test/"
			,"/home/walker/test"
			,"/home/walker/test/web.log.1"
			,"/home/walker/test.1/web.log"
			,"/home/walker/test.1/web.log.1"
			,"web.log"
			,"web"
			,"/web.log"

			,"\\home\\walker\\test\\web.log"
			,"\\home\\walker\\test\\"
			,"\\home\\walker\\test"
			,"\\home\\walker\\test\\web.log.1"
			,"\\home\\walker\\test.1\\web.log"
			,"\\home\\walker\\test.1\\web.log.1"
			,"web.log"
			,"web"
			,"\\web.log"




	);
	List<List<String>> pathsR = Arrays.asList(
			Arrays.asList("/home/walker/test", "web.log", "web", "log")
			,Arrays.asList("/home/walker/test", "", "", "")
			,Arrays.asList("/home/walker", "test", "test", "")
			,Arrays.asList("/home/walker/test", "web.log.1", "web.log", "1")
			,Arrays.asList("/home/walker/test.1", "web.log", "web", "log")
			,Arrays.asList("/home/walker/test.1", "web.log.1", "web.log", "1")
			,Arrays.asList("", "web.log", "web", "log")
			,Arrays.asList("", "web", "web", "")
			,Arrays.asList("", "web.log", "web", "log")

			,Arrays.asList("\\home\\walker\\test", "web.log", "web", "log")
			,Arrays.asList("\\home\\walker\\test", "", "", "")
			,Arrays.asList("\\home\\walker", "test", "test", "")
			,Arrays.asList("\\home\\walker\\test", "web.log.1", "web.log", "1")
			,Arrays.asList("\\home\\walker\\test.1", "web.log", "web", "log")
			,Arrays.asList("\\home\\walker\\test.1", "web.log.1", "web.log", "1")
			,Arrays.asList("", "web.log", "web", "log")
			,Arrays.asList("", "web", "web", "")
			,Arrays.asList("", "web.log", "web", "log")



	);

    @Test
    public void testPathAll() {
    	boolean flag = true;
    	for(int i = 0; i < paths.size(); i++){
    		String key = paths.get(i);
    		List<String> res = pathsR.get(i);

			String path = FileUtil.getFilePath(key);
			String name = FileUtil.getFileName(key);
			String nameOnly = FileUtil.getFileNameOnly(key);
			String type = FileUtil.getFileType(key);

			List<String> res1 = Arrays.asList(path, name, nameOnly, type);

			List<Object> es = new ArrayList<>();
			for(int j = 0; j < res.size(); j++){
				if(res1.get(j).equalsIgnoreCase(res.get(j))){
					es.add("k");
				}else{
					es.add(j);
					flag = false;
				}
			}
			Tools.out(i, es, key, res1);

		}

		Assert.assertTrue(flag);

    }



}
