package com.walker.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.walker.core.encode.JsonFastUtil;
import com.walker.core.encode.JsonUtil;
import com.walker.util.Bean;
import com.walker.util.Tools;
import org.junit.Test;

import java.util.Map;

public class JsonFastUtilTest {

    @Test
    public void toString1() {


        Bean b = null;
        String beanStr = JsonFastUtil.toString(new Bean().put("key", "kv").put("bean",new Bean().put("inner", "value")));
        Bean b2 =  JsonFastUtil.get(beanStr, new TypeReference<Bean>(){});
        Tools.out(b2.getClass(), b2);

        Bean b3 =  JSON.parseObject(beanStr, new TypeReference<Bean>(){});
        Tools.out(b3.getClass(), "get new typer bean", b3);


        b =  JSON.parseObject(beanStr, Bean.class);
        Tools.out(b.getClass(), "get bean", b);

        String str = "{'hello':'world'}";
        Map<String, String> map = JsonFastUtil.get(str);
        Tools.out(map.getClass(), "get normal", map);


        b = JsonUtil.get(beanStr);
        Tools.out(b.getClass(), "get", b);


    }
}