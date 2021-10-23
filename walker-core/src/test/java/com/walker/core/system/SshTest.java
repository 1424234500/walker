package com.walker.core.system;

import com.walker.system.Server;
import com.walker.system.SshJsch;
import com.walker.util.Tools;
import org.junit.Test;

public class SshTest {

    @Test
    public void getConnection() {

        Server server = new Server("39.106.111.11", "walker", "");
        SshJsch ssh = new SshJsch(server);
        try {
            Tools.out(ssh.execute("ls;date;ll;echo $PATH"));
            Tools.out(ssh.execute("echo 1"));
            Tools.out(ssh.execute("echo 2"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}