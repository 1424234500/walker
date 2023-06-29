package com.walker.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListConnectUtilTest extends TestCase {


    public void test1() {
        List<Map<String, Object>> list1 = new ArrayList<>();
        List<Map<String, Object>> list2 = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", i);
            map1.put("one", i);
            list1.add(map1);

            Map<String, Object> map2 = new HashMap<>();
            map2.put("id", i);
            map2.put("double", i * i);
            list2.add(map2);
        }

        List<Object> list1Ids = list1.stream().map(item -> item.get("id")).collect(Collectors.toList());
        List<Map<String, Object>> listQueryByIds = list2;

        Map<Object, Map<String, Object>> mapQueryByIds = listQueryByIds.stream().collect(Collectors.toMap(item -> item.get("id"), Function.identity(), (key1, key2) -> key2));

        list1.forEach(item -> {
            Map<String, Object> queryObj = mapQueryByIds.get(item.get(("id")));
            if (queryObj != null) {
                item.put("two", queryObj.get("double"));
            }
        });
        System.out.println(JSON.toJSONString(list1, SerializerFeature.PrettyFormat));
        System.out.println("-----------");
    }

    public void test2() {
        List<Map<String, Object>> list1 = new ArrayList<>();
        List<Map<String, Object>> list2 = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", i);
            map1.put("one", i);
            list1.add(map1);

            Map<String, Object> map2 = new HashMap<>();
            map2.put("id", i);
            map2.put("double", i * i);
            list2.add(map2);
        }
        ListConnectUtil.connectBy(list1, new ListConnectUtil.IProperty<Map<String, Object>, Object, Map<String, Object>>() {
            @Override
            public Object getKey(Map<String, Object> itemObj) {
                return itemObj.get("id");
            }

            @Override
            public List<Map<String, Object>> getQueryResByKeys(List<Object> listIds) {
                return list2;
            }

            @Override
            public Object getKeyQuery(Map<String, Object> itemObj) {
                return itemObj.get("id");
            }

            @Override
            public void setProperty(int listIndex, Object o, Map<String, Object> itemObj, List<Map<String, Object>> stringObjectMap) {
                itemObj.put("twww", stringObjectMap);
            }
        });

        System.out.println(JSON.toJSONString(list1, SerializerFeature.PrettyFormat));
        System.out.println("-----------");
    }

}