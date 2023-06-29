package com.walker.service;


import com.walker.core.mode.school.Area;

import java.util.List;

/**
 * 初始化数据服务 地理信息
 */
public interface SyncAreaService {

    /**
     * 国家统计局 地区分级 遍历抓取数据
     */
    void syncArea();

    /**
     * 递归树 构建每个父子关系并 便利出list方便存储
     * @param area
     * @param pid
     * @param idPath
     * @param namePath
     */
    List<Area> tree2list(Area area, String pid, String idPath, String namePath);



    /**
     * 获取china root
     */
    Area getCityRootChina();

    /**
     *  遍历节点 字节点 生成树 或 获取list
     *
     * @param parent    root节点
     * @param ifChild 是否递归子节点
     * @param list  若不为null 则把节点都添加进去   大量数据不应当使用
     * @return int 返回异常html次数
     */
    int getCity(Area parent, boolean ifChild, List<Area> list);


    /**
     * 是否已经处理
     * @param area
     */
    boolean isExists(Area area);

    /**
     * 已经处理
     * @param area
     */
    boolean setDone(Area area);


}
