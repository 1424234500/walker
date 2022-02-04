package com.walker.config;

import com.walker.dao.TeacherRepository;
import com.walker.mode.school.Teacher;
import com.walker.util.TimeUtil;
import com.walker.util.Tools;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;


@SpringBootTest
public class CacheConfigTest {
    @Autowired
    TeacherRepository teacherRepository;


    @Test
    public void cacheManagerSimpleKey() {
        StopWatch sw = new StopWatch();
        for(int i = 0; i < 5; i++){
            sw.start("save" + i);
            Tools.out("save" + i, teacherRepository.save(new Teacher().setID("" + i+"").setNAME("name" + i).setS_ATIME(TimeUtil.getTimeYmdHms())));
            sw.stop();
        }
        String id = "";
        int i = 0;
        id = "0";
        sw.start("findOneJPQLWithCacheSimpleKey  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCacheSimpleKey(id));
        sw.stop();
        sw.start("findOneJPQLWithCacheSimpleKey  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCacheSimpleKey(id));
        sw.stop();
        sw.start("findOneJPQLWithCacheSimpleKey  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCacheSimpleKey(id));
        sw.stop();
        sw.start("findOneJPQLWithCacheSimpleKey  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCacheSimpleKey(id));
        sw.stop();


        sw.start("deleteJPQLWithCacheSimpleKey  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.deleteJPQLWithCacheSimpleKey(id));
        sw.stop();

        sw.start("findOneJPQLWithCacheSimpleKey  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCacheSimpleKey(id));
        sw.stop();
        sw.start("findOneJPQLWithCacheSimpleKey  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCacheSimpleKey(id));
        sw.stop();




        Tools.out(sw.prettyPrint());




    }


    @Test
    public void cacheManagerKeyGenerator() {

        StopWatch sw = new StopWatch();
        sw.start("saveWithCache" );

        try {
            for (int i = 5; i < 10; i++) {
                Tools.out("saveWithCache" + i, teacherRepository.saveWithCache("" + i, "name" + i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        sw.stop();

        String id = "";
        int i = 0;
        id = "0";
        sw.start("findOneJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCache(id));
        sw.stop();
        sw.start("findOneJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCache(id));
        sw.stop();
        sw.start("findOneJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCache(id));
        sw.stop();
        sw.start("findOneJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCache(id));
        sw.stop();
        sw.start("findOneJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCache(id));
        sw.stop();

        sw.start("deleteJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.deleteJPQLWithCache(id));
        sw.stop();

        sw.start("findOneJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCache(id));
        sw.stop();
        sw.start("findOneJPQLWithCache  " + id + " c:" + i++);
        Tools.out(id, teacherRepository.findOneJPQLWithCache(id));
        sw.stop();

        Tools.out(sw.prettyPrint());

    }


    @Test
    public void keyGenerator() {

    }
}