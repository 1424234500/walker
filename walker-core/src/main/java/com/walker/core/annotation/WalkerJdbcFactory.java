package com.walker.core.annotation;

import com.walker.core.database.BaseDao;
import com.walker.core.util.ProxyUtil;


/**
 * 获取代理对象
 */

public class WalkerJdbcFactory {

    public static <T> T getInstance(Class<T> interfac, BaseDao dao) {
        return ProxyUtil.getProxy(interfac, new WalkerJdbcProxy().setBaseDao(dao));
    }


}



