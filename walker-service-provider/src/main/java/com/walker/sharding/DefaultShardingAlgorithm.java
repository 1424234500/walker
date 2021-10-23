package com.walker.sharding;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


/**
 * 生成表名 可动态 去掉实际表名区间 按日期生成表
 */
public class DefaultShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        String value = preciseShardingValue.getValue();
        int c = Math.abs(value.hashCode());
        int cc = c % collection.size(); // 20 : 0 -> 1
        int i = 0;
        for (String tableName : collection) { //W_MAN_0,W_MAN_1,,,W_MAN_10,W_MAN_11
            if (i == cc) {
                log.info("table sharding " + tableName + " " + collection.size() + "/" + cc);
                return tableName;
            }
            i++;
        }
        throw new RuntimeException("route table exception ??? " + " " + value + " count:" + cc + "/" + collection.size());
    }
}