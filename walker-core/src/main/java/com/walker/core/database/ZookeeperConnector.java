package com.walker.core.database;

import com.walker.core.aop.FunArgsReturn;
import com.walker.core.cache.LockHelpZookeeper;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zk
 * 接入工具模型
 * eg: *test
 */
public class ZookeeperConnector extends LockHelpZookeeper implements Watcher {
    private final static Logger log = LoggerFactory.getLogger(ZookeeperConnector.class);
    /**
     * 用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行
     */
    private final CountDownLatch signal = new CountDownLatch(1);
    protected String connectString = "127.0.0.1:2881,127.0.0.1:2880,127.0.0.1:8096";
    protected Integer sessionTimeout = 10000;
    protected Watcher watcher = this;
    protected Long sessionId = null;
    protected String sessionPasswd = "";
    protected Boolean canBeReadOnly = false;
    private ZooKeeper zookeeper;


    @Override
    public String info() {
        return this.toString();
    }

    @Override
    public Boolean check() throws Exception {
        Long res = this.doZooKeeper(zooKeeper -> {
            String url = "test";
            return ZooKeeperUtil.delete(zooKeeper, true, url);
        });
        log.info("test res " + res);
        return super.check();
    }

    @Override
    public Boolean init() throws Exception {
        this.doZooKeeper(null);
        return super.init();
    }

    @Override
    public Boolean uninit() throws Exception {
        if (this.zookeeper != null)
            this.zookeeper.close();
        return super.uninit();
    }

    /**
     * 对象内单例 迟加载 双重锁
     */
    private ZooKeeper getZooKeeper() {
        if (this.zookeeper == null) {
            synchronized (this) {
                if (this.zookeeper == null) {
                    log.info("init begin " + this);
//                    this.zookeeper = new ZooKeeper(connectString, sessionTimeout, watcher, sessionId, sessionPasswd.getBytes(), canBeReadOnly); //迷之npe
                    try {
                        zookeeper = new ZooKeeper(connectString, sessionTimeout, this);
                        signal.await(sessionTimeout, TimeUnit.MILLISECONDS);
                        log.info("init ok " + this);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return this.zookeeper;
    }

    public <T> T doZooKeeper(FunArgsReturn<ZooKeeper, T> fun) {
        if (fun != null) {
            return fun.make(this.getZooKeeper());
        }
        return null;
    }

    /**
     * 收到来自Server的Watcher通知后的处理。
     */
    @Override
    public void process(WatchedEvent event) {
        if (event == null) {
            return;
        }
        // 连接状态
        Event.KeeperState keeperState = event.getState();
        // 事件类型
        Event.EventType eventType = event.getType();
        // 受影响的path
        String path = event.getPath();
        log.debug(eventType + " " + event + " " + keeperState);
    }

    public ZookeeperConnector setConnectString(String connectString) {
        this.connectString = connectString;
        return this;
    }

    public ZookeeperConnector setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public ZookeeperConnector setWatcher(Watcher watcher) {
        this.watcher = watcher;
        return this;
    }

    public ZookeeperConnector setSessionId(Long sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ZookeeperConnector setSessionPasswd(String sessionPasswd) {
        this.sessionPasswd = sessionPasswd;
        return this;
    }

    public ZookeeperConnector setCanBeReadOnly(Boolean canBeReadOnly) {
        this.canBeReadOnly = canBeReadOnly;
        return this;
    }

    @Override
    public String toString() {
        return "ZookeeperModel{" +
                "connectString='" + connectString + '\'' +
                ", sessionTimeout=" + sessionTimeout +
                ", sessionId=" + sessionId +
                ", sessionPasswd='" + sessionPasswd + '\'' +
                ", canBeReadOnly=" + canBeReadOnly +
                '}';
    }

}
