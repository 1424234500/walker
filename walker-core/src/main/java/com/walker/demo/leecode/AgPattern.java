package com.walker.demo.leecode;


import java.util.HashMap;
import java.util.Map;

/**
 * abba 浙江 杭州 杭州 浙江 模式匹配规则
 */
public class AgPattern {


    public static void main(String[] args) throws Exception {
        System.out.println(new AgPattern().fun("abba", "浙江 杭州 杭州 浙江"));
        System.out.println(new AgPattern().fun("aaba", "浙江 杭州 杭州 浙江"));
        System.out.println(new AgPattern().fun("baab", "浙江 杭州 杭州 浙江"));
        System.out.println(new AgPattern().fun("abcd", "浙江 杭州 杭州1 浙江2"));

    }

    boolean fun(String pattern, String to) {

        String[] tos = (to.split(" +"));

        char[] ps = pattern.toCharArray();

        Map<Character, String> map = new HashMap<>();
        for (int i = 0; i < ps.length; i++) {
            String last = map.get(ps[i]);
            String now = tos[i];
            if (last == null) {
                map.put(ps[i], now);
            } else if (last.equalsIgnoreCase(now)) {

            } else {
                return false;
            }
        }
        return true;
    }


}
