package com.walker.service;


import com.walker.core.mode.SqlColumn;
import com.walker.dao.JdbcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 缓存服务
 */
@Service("cacheService")
public class CacheService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    JdbcDao jdbcDao;


    /**
     * 使用redis sb 缓存
     * @param dbOrUser
     * @param tableName
     */
    @Cacheable(keyGenerator="keyGenerator",value="cache-getColsMapCache")
    public List<SqlColumn> getColsMapCache(String dbOrUser, String tableName){
        return jdbcDao.getColumnsByDbAndTable(dbOrUser, tableName);
//        return ConfigMgr.getInstance().getFun("jdbcDao.getColumnsMapByTableName:" + dbOrUser + ":" + tableName, new Cache.Function() {
//            @Override
//            public Map<String, String> cache() {
//                return jdbcDao.getColumnsMapByTableName(dbOrUser, tableName);
//            }
//        });
    }

}
