package com.walker.socket.frame;

public interface SocketReadOkError {

    void readSuccess(String line);

    /**
     *
     * @param e
     * @return true 则继续读取数据 false 抛出异常
     */
    boolean readException(Throwable e);


}
