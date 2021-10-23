package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.mode.Teacher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

public class TeacherRepositoryTest extends ApplicationProviderTests {



    @Autowired
    TeacherRepository teacherRepository;

    @Before
    public void setUp() throws Exception {
        makeData();
    }

    @After
    public void tearDown() throws Exception {
        teacherRepository.selfDeleteAll(Arrays.asList("1,2,3".split(",")));
    }

    @Test
    public void findALl(){
        out("1,2,3", teacherRepository.selfFindListCacheJPQL(Arrays.asList("1,2,3".split(","))));
        out("1,2,3", teacherRepository.selfFindListCacheJPQL(Arrays.asList("1,2,3".split(","))));
        out("1,2", teacherRepository.selfFindListCacheJPQL(Arrays.asList("1,2".split(","))));
        out("1", teacherRepository.selfFindListCacheJPQL(Arrays.asList("1".split(","))));
//        out(teacherRepository.selfFindListCacheJPQL(Arrays.asList()));

    }

    public void makeData(){
        teacherRepository.selfDeleteAll(Arrays.asList("1,2,3".split(",")));
        teacherRepository.save(new Teacher().setID("1").setNAME("111"));
        teacherRepository.save(new Teacher().setID("2").setNAME("222"));
        teacherRepository.save(new Teacher().setID("3").setNAME("333"));
    }
    @Test
    public void testFindPage(){
        out("findById", teacherRepository.findById("1"));
        out("findAllById", teacherRepository.findAllById(Arrays.asList("1,2,3".split(","))));

        out(teacherRepository.selfCount(""));
        out(teacherRepository.selfCount(null));
        out(teacherRepository.selfCount("1"));
        out(teacherRepository.count());

    }
    @Test
    public void testFindName(){
        Pageable pageable = PageRequest.of(0, size);

        out(teacherRepository.selfFindPage(null, pageable));
        out(teacherRepository.selfFindPage("", pageable));
        out(teacherRepository.selfFindPage("1", pageable));
        out(teacherRepository.selfFindPage("add", pageable));

    }
    @Test
    public void selfUpdateSql() {
        Pageable pageable = PageRequest.of(0, size);

        out(teacherRepository.selfUpdateSql("nameupdate", "1"));
        out(teacherRepository.selfFindPage("", pageable));
        makeData();
    }

    @Test
    public void saveWithCache() {
        Pageable pageable = PageRequest.of(0, size);

        out(teacherRepository.saveWithCache("1", "nameupdate2"));
        out(teacherRepository.selfFindPage("", pageable));
        makeData();
    }

    @Test
    public void findOneJPQLWithCache() {
        out(teacherRepository.findOneJPQLWithCache("1"));
    }

    @Test
    public void selfFindListCacheJPQL() {
        out(teacherRepository.selfFindListCacheJPQL(Arrays.asList("1,2,3".split(","))));
    }

    @Test
    public void deleteJPQLWithCache() {
        out(teacherRepository.deleteJPQLWithCache("1"));
        out(teacherRepository.findOneJPQLWithCache("1"));
        makeData();
    }

    @Test
    public void selfDeleteAll() {
        Pageable pageable = PageRequest.of(0, size);

        out(teacherRepository.selfDeleteAll(Arrays.asList("1,2,3".split(","))));
        out(teacherRepository.selfFindPage("", pageable));
        makeData();
    }


    @Test
    public void selfFindByName() {
        out(teacherRepository.selfFindByName("111"));
    }

    @Test
    public void selfCount() {
        out(teacherRepository.selfCount("111"));
    }

    @Test
    public void selfFindPageOnceSql() {
        Pageable pageable = PageRequest.of(0, size);
        Page<Teacher> a = teacherRepository.selfFindPageOnceSql("111", pageable);
        out(a.getTotalElements());
        out(a.getContent());
    }

    @Test
    public void selfFindPageOnceJpql() {
        Pageable pageable = PageRequest.of(0, size);
        Page<Teacher> a = teacherRepository.selfFindPageOnceJpql("111", pageable);
        out(a.getTotalElements());
        out(a.getContent());
    }


}