package com.walker;

import com.alibaba.fastjson.JSON;
import com.walker.core.mode.Bean;
import com.walker.core.mode.school.Student;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestFastJson {


    @Test
    public void test(){

        Student stu = new Student();
        stu.setID("id");
        stu.setNAME("name");
        stu.setS_MTIME("time");
        Response res = Response.makeTrue("info", Arrays.asList(stu,new Bean().set("tc", "tc")));
        String str = JSON.toJSONString(res);
        System.out.println(str);
        Response tt = JSON.parseObject(str, Response.class);
        System.out.println(tt);
        List<Object> list = tt.getData();
        System.out.println(list.get(0));
        System.out.println(list.get(1));
//        Bean b = (Bean) list.get(1);
//        System.out.println(b.get("tc"));



    }


}
