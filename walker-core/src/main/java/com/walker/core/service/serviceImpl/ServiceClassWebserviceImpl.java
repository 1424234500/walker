package com.walker.core.service.serviceImpl;

import com.walker.core.service.service.ServiceClass;

import javax.jws.WebService;
import java.rmi.RemoteException;

/**
 *
 */
@WebService
public class ServiceClassWebserviceImpl implements ServiceClass {

	@Override
	public String test(String name) {
		return "echo." + name;
	}

	@Override
	public Object doClassMethod(String className, String methodName, Object... methodArgs) throws RemoteException {
		return new ServiceClassImpl().doClassMethod(className, methodName, methodArgs);
	}

}
