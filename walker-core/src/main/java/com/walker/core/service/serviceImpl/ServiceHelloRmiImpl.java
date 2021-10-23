package com.walker.core.service.serviceImpl;

import com.walker.core.service.service.ServiceHelloRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * service Rmi需要异常
 */
public class ServiceHelloRmiImpl extends UnicastRemoteObject implements ServiceHelloRmi {
    private static final long serialVersionUID = 1L;

    public ServiceHelloRmiImpl() throws RemoteException {
        super();
    }

    public String sayHello(String name) throws RemoteException {
        return "Hello " + name;
    }

}