package com.walker;

import com.walker.core.util.TimeUtil;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class ApplicationProviderTests {
    public static int size = 8;


    static{



    }
    public void out(Object...objects){
        System.out.println(TimeUtil.getTimeYmdHmss() + ": " + Arrays.toString(objects));
    }
    public void test(){
        out("hello test" );
    }


}
