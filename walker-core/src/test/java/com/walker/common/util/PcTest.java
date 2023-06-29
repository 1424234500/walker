package com.walker.common.util;

import com.walker.core.system.Pc;
import com.walker.core.util.Tools;
import org.junit.Test;

import java.io.IOException;

public class PcTest {

    @Test
    public void getRuntime() {
       Tools.out( Pc.getRuntime());
    }

    @Test
    public void ping() {
        Tools.out(Pc.ping("127.0.0.1", 200));
    }

    @Test
    public void telnet() {
        Tools.out(Pc.telnet("127.0.0.1", 6379, 200));
    }

    @Test
    public void getCpu() throws IOException, InterruptedException {
        Tools.out("cpu", Pc.getCpu());
        Tools.out(Pc.doCmdString("top -bn 2 "));
//        Tools.out(Pc.doCmdString("top -bn1"));
        Tools.out(Pc.doCmdString("echo ----------------"));
        Tools.out(Pc.doCmdString("ls -alF"));

    }
}