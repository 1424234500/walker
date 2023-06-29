package com.walker.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Map工厂类 new MapBuilder().set("key","value").build();
 *
 * @author Walker
 * 2017年9月14日15:08:12
 */
public class MapBuilder<KEY, VALUE> {
    Map<KEY, VALUE> map;

    public MapBuilder() {
        map = new HashMap<>();
    }

    public MapBuilder put(KEY key, VALUE value) {
        map.put(key, value);
        return this;
    }

    public Map<KEY, VALUE> build() {
        return map;
    }

}