package com.walker.core.mode;

import com.walker.core.util.Tools;
import org.junit.Assert;
import org.junit.Test;

public class UnsafeSuperArrayTest {

    @Test
    public void set() {
        int maxv = 10;  //内存oom Integer.MAX_VALUE;
//        UnsafeSuperArray array = new UnsafeSuperArray( (long)maxv * 2 );
        UnsafeSuperArray array = new UnsafeSuperArray( (long)maxv * 2 );
        System.out.println("Array size:" + array.size()); // 4294967294
        int sum=0;
        for (int i = 0; i < 100; i++) {
            array.set((long)maxv + i, (byte)3);
            sum += array.get((long)maxv + i);
        }
        System.out.println(sum);
        array.set(maxv + 998, (byte)6);

        int res = array.get(maxv + 998);
        Tools.out("get", res, res == 6);
        Assert.assertTrue(res == 6);

    }
}