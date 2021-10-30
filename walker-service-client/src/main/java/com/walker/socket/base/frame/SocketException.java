package com.walker.socket.base.frame;

import org.apache.log4j.Logger;

import java.util.Arrays;


public class SocketException extends Exception {
    private static final Logger log = Logger.getLogger(SocketException.class);

    /**
     *
     */
    private static final long serialVersionUID = 5002931016898035011L;

    public SocketException(String str) {
        super(str);
    }

    public SocketException(Object... objs) {
        this(Arrays.toString(objs));
        log.error(Arrays.toString(objs), this);
    }

}
