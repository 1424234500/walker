package com.walker.core.cache;

import com.walker.mode.CacheModel;
import com.walker.mode.Page;
import com.walker.util.LangUtil;
import com.walker.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 缓存服务实现类，直接采用内存做缓存服务
 * 缓存池支持 ?
 * 缓存时间支持
 */
public abstract class CacheMap extends LockAdapter implements Cache<String> {
    private Logger log = LoggerFactory.getLogger(CacheMap.class);


    /** 存储缓存 */
//    private Map<String, Object> map = new ConcurrentHashMap<>();
//    private Map<String, Index> mapIndex = new ConcurrentHashMap<>();
    public abstract Map<String, Object> getMap();
    public abstract Map<String, Index> getMapIndex();

    //map非redis单例无需分区
    @Override
    public String getKeyCache(String key){
        return key;
    }

	/**
	 * 附加数据记录
	 */
	public class Index{
		Long count = 0L;	//命中次数
		Long timemillExpire;	//过期时间
		Long mtime;	//修改时间
		public Boolean isExpire(){ //是否过期
			if(timemillExpire < 0) return false;
			return System.currentTimeMillis() > mtime + timemillExpire;
		}
	}
	
    
    
    
    public CacheMap(){
		log.info("CacheMap init");
    }
    
	@Override
	public String toString() {
		return "Cache map:" + getMap().toString();
	}

    @Override
	public Long size() {
		return Long.valueOf(getMap().size());
	}

	@Override
	public Boolean containsKey(String key) {

		if(getMap().containsKey(key) && getMapIndex().containsKey(key)){
			Index index = getMapIndex().get(key);
			if(index.isExpire()){
				remove(key);
				return false;
			}
		}else{
			remove(key);
			return false;
		}
		return true;
	}

	@Override
	public Long clear() {
        Long res = size();
		getMap().clear();
		getMapIndex().clear();
		return res;
	}

	@Override
	public Set<String> keySet() {
		return getMap().keySet();
	}

	@Override
	public <V> V get(String key, V defaultValue) {
		if(containsKey(key)){
			Index index = getMapIndex().get(key);
			index.count += 1;
			return (V)(LangUtil.turn(getMap().get(key), defaultValue));
		}
		return defaultValue;
	}

	@Override
	public <V> Cache<String> put(String key, V value, Long timemillExpire) {
		getMap().put(key, value);
		Index index;
		if(getMapIndex().containsKey(key)){
			index = getMapIndex().get(key);
		}else{
			index = new Index();
			getMapIndex().put(key, index);
		}
		index.mtime = System.currentTimeMillis();
		index.timemillExpire = timemillExpire;
		return this;
	}

	@Override
	public Long remove(String key) {
		getMap().remove(key);
		getMapIndex().remove(key);
		return 1L;
	}


    @Override
    public List<CacheModel> findPage(String args, Page page) {
        List<CacheModel> list = new ArrayList<>();
        Iterator<String> iterator = getMap().keySet().iterator();
        page.setNum(getMap().size());
        for(int i = 0; i < getMap().size() && i < page.getStop(); i++){
            String key = iterator.next();
            if(i < page.getStart()) continue;

            Index index = getMapIndex().get(key);
            Object value = get(key);
			CacheModel res = new CacheModel();
            res.setHASHCODE("" + key.hashCode());
            res.setKEY(key);
            res.setVALUE(value);
            if(index != null) {
                res.setMTIME(TimeUtil.getTimeYmdHms(index.mtime));
                res.setEXPIRE("" + index.timemillExpire);
                res.setCOUNT("" + index.count);
            }
            res.setTYPE(value == null ? null : value.getClass().getSimpleName());
            list.add(res);
        }
        return list;
    }


}
