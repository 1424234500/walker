package com.walker.service;

import com.walker.mode.Page;
import com.walker.mode.Teacher;

import java.util.List;

public interface TeacherService {


    List<Teacher> saveAll(List<Teacher> objs);
    Integer[] deleteAll(List<String> ids);

    Teacher get(Teacher obj);
    Integer delete(Teacher obj);

    List<Teacher> finds(Teacher obj, Page page);
    Integer count(Teacher obj);



}
