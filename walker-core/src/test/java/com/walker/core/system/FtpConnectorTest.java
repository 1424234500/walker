package com.walker.core.system;

import junit.framework.TestCase;

import java.io.File;

public class FtpConnectorTest extends TestCase {

    public void test(){
        String[] argv = {};
        String[] argvd = new String[]{"4", "127.0.0.1","root","", "/home/test.txt", "D:\\ttt\\"};
        for (int i = 0; i < argv.length; i++) {
            argvd[i] = argv[i];
        }
        argv = argvd;
        String from = argv[4];
        String downdir = argv[5];
        IpModel ipModel = new IpModel(argv[1],argv[2],argv[3]);
        FtpConnector server = new FtpConnector(ipModel);

        server.setMoreLog(true);
        System.out.println("" + server.upload(downdir + "test.txt", new File(from).getParent() + File.separator + "test.upload.txt").toInfo());
        server.setMoreLog(false);
        String to = downdir + "tno-" + "-no-" + "-name-" + new File(from).getName();
        System.out.println("" + server.download(from, to).toInfo());
    }

}