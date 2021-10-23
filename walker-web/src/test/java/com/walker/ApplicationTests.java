package com.walker;

import com.walker.core.annotation.WalkerJdbcScan;
import com.walker.spring.ImportBeanConfig;
import com.walker.util.TimeUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@Import(ImportBeanConfig.class)
@WalkerJdbcScan("com.walker.mapper")
@SpringBootTest
public class ApplicationTests {
    public static int size = 8;


    static{



    }
    public void out(Object...objects){
        System.out.println(TimeUtil.getTimeYmdHmss() + ": " + Arrays.toString(objects));
    }
    @Test
    public void test(){
        out("hello test" );
    }


}
