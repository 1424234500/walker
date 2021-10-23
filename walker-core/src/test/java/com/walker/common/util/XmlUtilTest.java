package com.walker.common.util;

import com.walker.core.encode.JsonUtil;
import com.walker.core.encode.XmlUtil;
import com.walker.util.Bean;
import com.walker.util.FileUtil;
import com.walker.util.Tools;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.IOException;

public class XmlUtilTest {

    @Test
    public void toFullXml() throws DocumentException, IOException {
        String file = "/home/walker/test.xml";
        FileUtil.saveAs("<html href='www.baidu.com'> <img src='www.ddf.png' >aacb</img> </html>", file, false);

        Object bean = XmlUtil.parseFile(file);
//    	debug(JsonUtil.makeJson(bean, 0));
        debug(JsonUtil.makeJson(bean, 6));


        String path = ClassLoader.getSystemResource("").getPath() + "plugin.json";
        String str = FileUtil.readByLines(path, null, "utf-8");

        Bean bb = JsonUtil.get(str);
        String s = XmlUtil.turnElement(bb);
        debug(s);

    }
    private static void debug(Object...objects) {
        Tools.out(objects);
    }
}