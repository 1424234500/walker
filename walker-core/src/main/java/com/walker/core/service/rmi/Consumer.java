package com.walker.core.service.rmi;

import com.walker.core.cache.ConfigMgr;
import com.walker.core.service.service.ServiceClass;
import com.walker.core.util.Tools;

import java.rmi.Naming;


/**
 * Consumer
 */
public class Consumer {

    public void test() {
        int port = ConfigMgr.getInstance().get("port_rmi", 8091);

        try {
            ServiceClass hello = (ServiceClass) Naming.lookup("rmi://localhost:" + port + "/ServiceClass");
            Tools.out(hello.test("hello rmi"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}