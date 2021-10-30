package com.walker.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 统计sql
 *
 */
@Mapper
public interface StatisticsMapper {

    /**
     * 多指标  按   接口展开
     * X      Y1        Y2              Y3          Y4              Y5
     * 接口名  成功次数  成功平均耗时      失败次数      失败平均耗时     成功率0～100
     */
    List<Map<String, Object>> findAction(@Param("from") String from, @Param("to") String to);

    /**
     * 查找url类别集合
     */
    List<Map<String, Object>> findActionUrl(@Param("from") String from, @Param("to") String to);

    /**
     * 多指标  按   时间展开
     * X      Y1        Y2              Y3          Y4              Y5
     * 时间戳  成功次数  成功平均耗时      失败次数      失败平均耗时     成功率0～100
     */
    List<Map<String, Object>> findActionDetail(@Param("from") String from, @Param("to") String to, @Param("url") String url);


    /**
     * 多指标  按   时间展开
     * X      Y1        Y2              Y3          Y4              Y5
     * 时间戳  成功次数  成功平均耗时      失败次数      失败平均耗时     成功率0～100
     */
    List<Map<String, Object>> findUserData(@Param("from") String from, @Param("to") String to);


    /**
     * 多指标  按   接口展开
     * X      Y1        Y2              Y3          Y4              Y5  y6
     * 接口名  net次数  net耗时      wait次数      wait耗时
     */
    List<Map<String, Object>> findSocketDetail(@Param("from") String from, @Param("to") String to, @Param("plugin") String plugin);
    /**
     * 查找url类别集合
     */
    List<Map<String, Object>> findSocketDetailUrl(@Param("from") String from, @Param("to") String to);


    /**
     * 时间区间内 多条件筛选构图
     */
    List<Map<String, Object>> findDubboDetail(@Param("from") String from, @Param("to") String to, @Param("url") String url, @Param("consumer") String consumer, @Param("provider") String provider, @Param("type") String type);
    List<Map<String, Object>> findDubboDetailUrl(@Param("from") String from, @Param("to") String to);



}
