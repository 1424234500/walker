package com.walker.service;

import com.walker.core.mode.Page;
import com.walker.core.mode.sys.SysConfig;

import java.util.List;

public interface SysConfigService {


    List<SysConfig> saveAll(List<SysConfig> obj);
    Integer[] deleteAll(List<String> ids);

    SysConfig get(SysConfig obj);
    Integer delete(SysConfig obj);

    List<SysConfig> finds(SysConfig obj, Page page);
    Integer count(SysConfig obj);

}
