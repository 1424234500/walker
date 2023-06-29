package com.walker.core.annotation;

import com.walker.core.util.Tools;

/**
 * 注解使用样例
 */
public class EgTest {

    @Test
    public boolean test1() {
        Tools.out("hello annotation test1 ");
        return true;
    }


}