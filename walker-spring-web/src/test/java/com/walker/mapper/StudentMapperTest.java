package com.walker.mapper;

import com.walker.ApplicationTests;
import com.walker.mode.Page;
import com.walker.mode.school.Student;
import com.walker.util.TimeUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class StudentMapperTest extends ApplicationTests {

    @Autowired
    StudentMapper studentMapper;
    static int size = 4;
    static List<Student> list = new ArrayList<>();
    static List<String> ids = new ArrayList<>();
    static {
        for(int i = 0; i < size; i++){
            Student student = new Student().setID("id_test_" + i).setNAME(i % 2 == 0 ? "test0_name_" + i : "test1_name_" + i);
            list.add(student);
            ids.add(student.getID());
        }
    }
    @Test
    public void test() {
        int res = 0;
        out(list);
        res = studentMapper.insert(list);
        out("insert", res == size, res, size);

        List<Student> list0 = studentMapper.findPage(new Student().setNAME("test1"), new Page());
        out("findPage0", list0.size() == size / 2, list0);

        for(int i = 0; i < size; i++){
            Student student = list.get(i);
            student.setS_MTIME(TimeUtil.getTimeYmdHmss());
        }
        res = studentMapper.update(list);
        out("update",  res == 1, res, size);

        List<Student> list1 = studentMapper.findPage(new Student().setNAME("test1"), new Page());
        out("findPage1", list1.size() == size / 2, list1);

        res = studentMapper.count(new Student().setNAME("test1"));
        out("count",  res == size / 2, res / 2, size);
        res = studentMapper.delete(ids);
        out("delete",  res == size, res, size);
        res = studentMapper.count(new Student());
        out("count",  res == 0, res, size);


    }
}