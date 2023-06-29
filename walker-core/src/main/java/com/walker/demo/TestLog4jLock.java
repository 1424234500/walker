package com.walker.demo;

import com.walker.core.util.ThreadUtil;
import com.walker.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * log4j死锁问题验证
 */
public class TestLog4jLock {
    private static final Logger log = LoggerFactory.getLogger(TestLog4jLock.class);

    public static void main(String[] args) {
        startT(1);
        startT(2);
        int i = 0;
        log.info("e " + i++ + new StringCost(1000));
//		log.info("e " + i++ + new StringCost(1000).toString() );
//		log.info("e " + i++ + new StringCost(1000).toString() );
//		log.info("e " + i++ + new StringCost(1000).toString() );
//		log.info("e " + i++ + new StringCost(1000).toString() );


        ThreadUtil.sleep(999999);
    }

    public static void startT(Object t) {
        new Thread() {
            public void run() {
                int i = 0;
                log.info(new StringCost(1000).toString());

                while (i++ < 5) {
                    log.info(t + " " + new StringCost(1000));
//					ThreadUtil.sleep(10);
                }
            }


        }.start();


    }

//	FORTRAN
//
//	用 FORTRAN 写所有的代码。如果老板问你为啥，你可以回答说它有很多非常有用的库，你用它可以节约时间。不过，用 FORTRAN 写出可维护代码的概率是 0，所以，要达到不可维护代码编程指南里的要求就容易多了。
//	用 ASM
//
//	把所有的通用工具函数都转成汇编程序。
//	用 QBASIC
//
//	所有重要的库函数都要用 QBASIC 写，然后再写个汇编的封包程序来处理 large 到 medium 的内存模型映射。
//	内联汇编
//
//	在你的代码里混杂一些内联的汇编程序，这样很好玩。这年头几乎没人懂汇编程序了。只要放几行汇编代码就能让维护代码的程序员望而却步。
//	宏汇编调用C
//
//	如果你有个汇编模块被C调用，那就尽可能经常从汇编模块再去调用C，即使只是出于微不足道的用途，另外要充分利用 goto, bcc 和其他炫目的汇编秘籍。

//源码和编译jar包代码不一致！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//定义常量覆盖专用词语
//	定义一个叫 TRUE 的静态常量。在这种情况下，其他程序员更有可能怀疑你干的不是好事，因为Java里已经有了内建的标识符 true。	

//未使用变量i=i

    static class StringCost {
        int cost = 10;

        StringCost(int cost) {
            this.cost = cost;
        }

        @Override
        public String toString() {
            ThreadUtil.sleep(cost);
            return TimeUtil.getTimeYmdHmss();
        }
    }

}
