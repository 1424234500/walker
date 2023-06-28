package com.walker.system;

import junit.framework.TestCase;

public class SshJschConnectorTest extends TestCase {
    public void testGetResult() {
        String downdir = "D:\\ttt\\";
        IpModel ipModel = new IpModel("127.0.0.1", "root", "");
        SshJschConnector s = new SshJschConnector(ipModel);
//scp -p root@127.0.0.1:/root/test.txt  D:\ttt\1.txt
        System.out.println("" + s.download("/home/walker/test.txt", downdir + "test.txt").toInfo());
    }
}