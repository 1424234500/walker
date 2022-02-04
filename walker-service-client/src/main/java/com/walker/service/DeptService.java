package com.walker.service;

import com.walker.mode.Page;
import com.walker.mode.school.Dept;

import java.util.List;

public interface DeptService {


    List<Dept> saveAll(List<Dept> obj);
    Integer[] deleteAll(List<String> ids);

    Dept get(Dept obj);
    Integer delete(Dept obj);

    List<Dept> finds(Dept obj, Page page);
    Integer count(Dept obj);


    List<Dept> findsRoot(Page page);



}
