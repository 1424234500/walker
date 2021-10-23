package com.walker.socket.client.impl;

public class ClientStartStopTest<DATA> extends Client<Integer, DATA> {
    int count = 0;

    public ClientStartStopTest(String ip, int port, String name) throws Exception {
        super(ip, port, name);
    }

    @Override
    public void eventLoopKeeper() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                log.info(Thread.currentThread().getName() + " of " + count++);
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                //中断信号只能被消费一次 ? 中不中断由自己决定，如果需要真真中断线程，则需要重新设置中断位，如果不需要，则不用调用
//                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
//      中断后进入这里 意味着关闭server
    }

    @Override
    public String readString(Integer socket) throws Exception {
        return "" + socket;
    }

    @Override
    public void writeString(Integer socket, String data) throws Exception {
    }

    @Override
    public String makeSocketKey(Integer socket) {
        return "" + socket;
    }
}
