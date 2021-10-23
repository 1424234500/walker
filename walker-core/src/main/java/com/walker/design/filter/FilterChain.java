package com.walker.design.filter;


import com.walker.core.cache.ConfigMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FilterChain<T, R> extends LinkedHashSet<Filter<T, R>> {
    private static final Logger log = LoggerFactory.getLogger(FilterChain.class);
    Iterator<Filter<T, R>> iterator;

    public static void main(String[] argv) {
        ConfigMgr.getInstance();
        FilterChain<Map, List> filterChain = new FilterChain<>();
        filterChain.add(new Filter<Map, List>() {
            @Override
            public List invoke(FilterChain<Map, List> filterChain, Map args) {
                log.info("filter a " + args);
                args.put("a", "a");
                List res = null;
                res = filterChain.invoke(args);
                return res;
            }

            @Override
            public String info() {
                return "this is a";
            }
        });

        filterChain.add(new Filter<Map, List>() {
            @Override
            public List invoke(FilterChain<Map, List> filterChain, Map args) {
                log.info("filter b " + args);
                args.put("b", "b");
                List res = null;
                res = filterChain.invoke(args);
                return res;
            }

            @Override
            public String info() {
                return "this is b";
            }
        });

        filterChain.add(new Filter<Map, List>() {
            @Override
            public List invoke(FilterChain<Map, List> filterChain, Map args) {
                log.info("filter c e " + args);
                args.put("c", "c");
                List res = null;
                throw new RuntimeException("c exception " + info());
            }

            @Override
            public String info() {
                return "this is c";
            }
        });

        Map args = new HashMap<>();
        List res = filterChain.invoke(args);
        log.info("filter res " + res + " args:" + args);

    }

    public R invoke(T args) {
        R res = null;
        if (iterator == null) {
            iterator = this.iterator();
        }
        if (iterator.hasNext()) {
            Filter<T, R> next = iterator.next();
            res = next.invoke(this, args);
        } else {
            log.info(" this is the last filter ");
        }
        return res;
    }


}

