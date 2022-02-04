package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.socket.model.Message;
import com.walker.util.TimeUtil;
import com.walker.util.Tools;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class MessageRepositoryTest extends ApplicationProviderTests {
    @Autowired
    MessageRepository messageRepository;

    @Test
    public void test(){
        Tools.out(messageRepository.save(new Message().setId("1").setText("hello_" + TimeUtil.getTimeYmdHmss())));
        List<Message> mans = new ArrayList<>();
        for(int i = 0; i < size; i++){
            mans.add(new Message().setId("id_" + i).setText("hello" + i + "_" + TimeUtil.getTimeYmdHmss()));
        }
        Tools.out("saveAll", messageRepository.saveAll(mans));
        Pageable pageable = PageRequest.of(0, size);
        Tools.formatOut(messageRepository.findAll(pageable).getContent());
        Tools.out(messageRepository.findById("1"));
//        Tools.out(manRepository.getOne("1")); //特殊功能?

    }




}