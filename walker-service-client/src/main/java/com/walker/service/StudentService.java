package com.walker.service;

import com.walker.mode.Page;
import com.walker.mode.Student;

import java.util.List;


/**
 * 案例：student表的hibernate和mybatis两种方式实现操作
 *
 */
public interface StudentService  {
	

    List<Student> saveAll(List<Student> objs);
    Integer[] deleteAll(List<String> ids);

    Student get(Student obj);
    Integer delete(Student obj);

    List<Student> finds(Student obj, Page page);
    Integer count(Student obj);



}