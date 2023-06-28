package com.walker.core.system;

import com.walker.system.IpModel;
import com.walker.system.SshJschConnector;
import com.walker.util.Tools;
import org.junit.Test;

public class SshTest {

    @Test
    public void getConnection() {

        IpModel ipModel = new IpModel("39.106.111.11", "walker", "");
        SshJschConnector ssh = new SshJschConnector(ipModel);
        try {
            Tools.out(ssh.execute("ls;date;ll;echo $PATH"));
            Tools.out(ssh.execute("echo 1"));
            Tools.out(ssh.execute("echo 2"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}