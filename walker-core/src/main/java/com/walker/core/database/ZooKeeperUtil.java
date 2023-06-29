package com.walker.core.database;

import com.walker.core.mode.CacheModelZk;
import com.walker.core.mode.Page;
import com.walker.core.util.LangUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 静态工具类模式 == 复制代码
 */
public class ZooKeeperUtil {
    private static final Logger log = LoggerFactory.getLogger(ZooKeeperUtil.class);

    /**
     * @param urll /
     * @return [
     * {URL:/dubbo, DATA:null, CHILD_SIZE:999, (childs) },...
     * ]
     */
    public static List<CacheModelZk> findPage(final ZooKeeper zooKeeper, final Boolean watch, final String urll, Page page) {
        List<CacheModelZk> res = new ArrayList<>();

        String url = urll;

        if (url == null) {
            url = "/";
        }
        try {
            Stat stat = zooKeeper.exists(url, watch);
            if (stat != null) {
                List<String> childrens = zooKeeper.getChildren(url, watch);
                log.debug("getChildren " + url + " " + childrens);

                for (String key : childrens) {
                    key = (url.endsWith("/") ? url : (url + "/")) + key;
                    Stat stat1 = zooKeeper.exists(key, watch);
//							List<String> listChildren = zooKeeper.getChildren(key, watch);
                    int size = stat1.getNumChildren(); //listChildren.size();
                    byte[] bs = zooKeeper.getData(key, watch, null);
                    String data = new String(bs == null ? new byte[]{} : bs);
                    String info = String.valueOf(LangUtil.turnObj2Map(stat1));   //stat1.toString();
                    List<ACL> acls = zooKeeper.getACL(key, stat);

                    CacheModelZk line = new CacheModelZk();
                    line.setKEY(key);
                    line.setVALUE(data);
                    line.setCHILD_SIZE(size);
                    line.setSTAT(info);
                    line.setACL(String.valueOf(acls));
//							line.put("CHILD", String.valueOf(listChildren));
                    res.add(line);
                }
            } else {
                log.warn(url + " zk findpage stat is null, not exists?");
            }
        } catch (Exception e) {
            log.error(url + " " + e.getMessage(), e);
        }
        return res;
    }

    public static Boolean create(final ZooKeeper zooKeeper, final Boolean watch, String url, String value) {
        try {
            if (zooKeeper.exists(url, watch) == null) {
                zooKeeper.create(url, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("zooeeper create " + url + " " + value);
                return true;
            }
        } catch (Exception e) {
            log.error("zooeeper create " + url + " " + value + " " + e.getMessage(), e);
        }
        return false;
    }

    public static Boolean createOrUpdateVersion(final ZooKeeper zooKeeper, final Boolean watch, String url, String value) {
        return createOrUpdateVersion(zooKeeper, watch, url, value, -1);
    }

    public static Boolean createOrUpdateVersion(final ZooKeeper zooKeeper, final Boolean watch, String url, String value, int version) {
        try {
            Stat stat = zooKeeper.exists(url, watch);
            if (stat == null) {
                zooKeeper.create(url, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("zooeeper createOrUpdate " + url + " " + value);
                return true;
            } else {
                int newVersion = version;
//						if(newVersion <= 0) {
//							newVersion = stat.getVersion();
//						}
                zooKeeper.setData(url, value.getBytes(), newVersion);
                log.info("zooeeper createOrUpdate " + url + " " + value);
                return true;
            }
        } catch (Exception e) {
            log.error("zooeeper createOrUpdate " + url + " " + value + " " + e.getMessage(), e);
        }
        return false;
    }

    public static Boolean exists(final ZooKeeper zooKeeper, final Boolean watch, String url) throws KeeperException, InterruptedException {
        return zooKeeper.exists(url, watch) != null;
    }

    /**
     * 递归删除字节点
     */
    public static Long delete(final ZooKeeper zooKeeper, final Boolean watch, String url) {
        Long res = 0L;
        try {
            Stat stat = zooKeeper.exists(url, watch);
            if (stat != null) {
                List<String> childrens = zooKeeper.getChildren(url, watch);
                for (String key : childrens) {
                    key = (url.endsWith("/") ? url : (url + "/")) + key;
                    res += (delete(zooKeeper, watch, key) > 0 ? 1 : 0);
                }
                zooKeeper.delete(url, -1);
                res++;
            }
        } catch (Exception e) {
            log.error("zooeeper delete " + url + " " + " " + e.getMessage(), e);
        }
        return res;
    }

    public static Long delete(final ZooKeeper zooKeeper, final Boolean watch, Set<String> urls) {
        Long res = 0L;
        try {
            for (String url : urls) {
                Stat stat = zooKeeper.exists(url, watch);
                if (stat != null) {
                    res += (delete(zooKeeper, watch, url) > 0 ? 1 : 0);
                }
            }
        } catch (Exception e) {
            log.error("zooeeper deletes " + urls + " " + res + " " + e.getMessage(), e);
        }
        return res;
    }

    public static <V> V get(ZooKeeper zooKeeper, boolean watch, String url, V defaultValue) {
        byte[] v = new byte[0];
        try {
            v = zooKeeper.getData(url, watch, null);
        } catch (Exception e) {
            log.error(url + " " + e.getMessage(), e);
        }
        return LangUtil.turn(v == null ? null : new String(v), defaultValue);
    }

    public static Boolean compareAndDelete(ZooKeeper zooKeeper, String lockKey, String value) {
        if (value.equals(get(zooKeeper, false, lockKey, null))) {
            return delete(zooKeeper, false, lockKey) > 0;
        }
        return false;
    }

}