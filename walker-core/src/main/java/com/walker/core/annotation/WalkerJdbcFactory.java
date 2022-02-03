package com.walker.core.annotation;

import com.walker.util.ProxyUtil;
import com.walker.core.database.BaseDao;


/**
 * 获取代理对象
 */

public class WalkerJdbcFactory {

    public static <T> T getInstance(Class<T> interfac, BaseDao dao) {
        return ProxyUtil.getProxy(interfac, new WalkerJdbcProxy().setBaseDao(dao));
    }


}



