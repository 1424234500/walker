package com.walker.service;

import com.walker.core.mode.Page;
import com.walker.mode.RoleUser;

import java.util.List;

public interface RoleUserService {


    List<RoleUser> saveAll(List<RoleUser> obj);
    Integer[] deleteAll(List<String> ids);

    RoleUser get(RoleUser obj);
    Integer delete(RoleUser obj);

    List<RoleUser> finds(RoleUser obj, Page page);
    Integer count(RoleUser obj);



}
