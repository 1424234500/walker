package com.walker.core.cache;

import com.walker.core.mode.CacheModel;
import com.walker.core.mode.Page;
import com.walker.core.mode.SqlColumn;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 仿造Map接口 缓存抽象接口
 */
public interface Cache<K> {

    /**
     * 分页查询
     */
    default List<SqlColumn> getCols() {
        return CacheModel.colMaps;
    }
	List<? extends CacheModel> findPage(String key, Page page);

    /**
     * 缓存时间
     * -1L  永不过期
     * 0L   立即过期
     */
    Long TIMEMILL_EXPIRE = 0L;
    Long TIMEMILL_EXPIRE_NEVER = -1L;


//    size contain
    Long size();
    default Boolean containsKey(K key){
        return get(key) != null;
    }

//    foreach
    default Long clear(){
        throw new RuntimeException("can't keySet redis");
    }

    default Set<K> keySet(){
        throw new RuntimeException("can't keySet redis");
    }

//    get set
    default <V> V get(K key){
        return get(key, null);
    }
    <V> V get(K key, V defaultValue);
    default <V> Cache<K> put(K key, V value){
        return put(key, value, TIMEMILL_EXPIRE_NEVER);
    }
    <V> Cache<K> put(K key, V value, Long timemillExpire);
    default <V> Long putAll(Map<K, V> map){
        for(Map.Entry<K, V> entry : map.entrySet()){
            put(entry.getKey(), entry.getValue());
        }
        return 1L * map.size();
    }
    Long remove(K key);

}
