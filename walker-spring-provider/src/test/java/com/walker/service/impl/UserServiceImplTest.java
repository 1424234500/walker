package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.mode.Page;
import com.walker.mode.school.Student;
import com.walker.mode.school.Teacher;
import com.walker.mode.school.User;
import com.walker.service.StudentService;
import com.walker.service.TeacherService;
import com.walker.service.UserService;
import com.walker.util.TimeUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImplTest extends ApplicationProviderTests {
    @Autowired
    UserService userService;
    @Autowired
    @Qualifier("teacherJpaService")
    TeacherService teacherService;
    @Autowired
    StudentService studentService;

    @Test
    public void addUser() {
        List<User> list = new ArrayList<>();
        for(int i = 0; i < 100; i++ ) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String seri = TimeUtil.getTime("SSS");
            list.add(new User()
                    .setID("id_" + seri)
                    .setNAME("name_" + seri)
                    .setPWD("pwd_" + seri)
                    .setDEPT_ID("" + seri)
                    .setEMAIL("" + seri + "@walker.com")
                    .setMOBILE("18408249" + seri)
                    .setNICK_NAME("nick_name_" + seri)
                    .setSIGN("sign_" + seri)
                    .setS_ATIME(TimeUtil.getTimeYmdHmss())
                    .setS_MTIME(TimeUtil.getTimeYmdHmss())
                    .setS_FLAG("1")
            );
        }
        out(list.size(), userService.saveAll(list));
    }



    @Test
    public void addTeacher(){
        List<Teacher> list = new ArrayList<>();
        for(int i = 0; i < 100; i++ ) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String seri = TimeUtil.getTime("SSS");
            list.add(new Teacher()
                    .setID("id_" + seri)
                    .setNAME("name_" + seri)

            );
        }
        out(list.size(), teacherService.saveAll(list));
    }

    @Test
    public void addStudent(){
        List<Student> list = new ArrayList<>();
        for(int i = 0; i < 100; i++ ) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String seri = TimeUtil.getTime("SSS");
            list.add(new Student()
                    .setID("id_" + seri)
                    .setNAME("name_" + seri)
                    .setCLASS_CODE("c_" + seri)
                    .setSEX(seri.compareTo("444") > 0 ? "1" : "0")

            );
        }
        out(list.size(), studentService.saveAll(list));


    }

    @Test
    public void finds() {
        Page page = new Page().setShownum(4);
        List<User> list = userService.finds(new User()
                .setID("id_1")
                .setNAME("name_1")
        , page);
        out(list);
        out(page);

    }

    @Test
    public void deleteAll() {
        out(userService.get(new User().setID("id_364")));

    }
}