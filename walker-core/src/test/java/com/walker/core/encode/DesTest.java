package com.walker.core.encode;

import com.walker.util.Tools;
import org.junit.Assert;
import org.junit.Test;

public class DesTest {
    String pwd = "password";
    String str = "select * from sy_org_user";
    @Test
    public void encrypt() throws Exception {

        String e = Des.encrypt(str, pwd);
        Tools.out(str, pwd, e);
        String s = Des.decrypt(e, pwd);
        Tools.out(e, pwd, s);
        Assert.assertEquals(s, str);

    }



}