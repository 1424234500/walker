package com.walker.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Map工厂类 new MakeMap().set("key","value").build();
 *
 * @author Walker
 * 2017年9月14日15:08:12
 */
public class MakeMap {
    Map<String, Object> map;

    public MakeMap() {
        map = new HashMap<>();
    }

    public MakeMap put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Map build() {
        return map;
    }

}