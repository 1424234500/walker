package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.core.mode.school.Man;
import com.walker.core.util.TimeUtil;
import com.walker.core.util.Tools;
import com.walker.spring.dao.ManRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ManRepositoryTest extends ApplicationProviderTests {
    @Autowired
    ManRepository manRepository;
    @Test
    public void test(){
        Man man = new Man();
        man.setId("1").setSex(0).setPwd("pwd").setName("name").setTime(TimeUtil.getTimeYmdHmss());
        Tools.out("save", manRepository.save(man));

        List<Man> mans = new ArrayList<>();
        for(int i = 0; i < size; i++){
            mans.add(new Man().setId("id_" + i).setSex(i%2).setPwd("pwd").setName("name").setTime(TimeUtil.getTimeYmdHmss()));
//            Tools.out("batch", i, manRepository.save(mans.get(i)));
        }
        Tools.out("saveAll", manRepository.saveAll(mans));
        Pageable pageable = PageRequest.of(0, size);
        Tools.formatOut(manRepository.findAll(pageable).getContent());
        Tools.out(manRepository.findById("1"));
//        Tools.out(manRepository.getOne("1")); //特殊功能?


    }

}