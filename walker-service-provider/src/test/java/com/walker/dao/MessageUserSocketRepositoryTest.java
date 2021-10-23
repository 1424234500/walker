package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.mode.MessageUser;
import com.walker.mode.MessageUserPK;
import com.walker.util.TimeUtil;
import com.walker.util.Tools;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class MessageUserSocketRepositoryTest extends ApplicationProviderTests {
    @Autowired
    MessageUserRepository messageUserRepository;

    @Test
    public void test(){
        Tools.out(messageUserRepository.save(new MessageUser().setId("1:all").setMsgId("msgid").setSmtime(TimeUtil.getTimeYmdHmss()).setUserFrom("from").setUserTo("to")));
        List<MessageUser> mans = new ArrayList<>();
        for(int i = 0; i < size; i++){
            mans.add(new MessageUser().setId("id_" + i).setMsgId("id_" + i % 3).setSmtime(TimeUtil.getTimeYmdHmss()).setUserFrom("from").setUserTo("to"));
        }
        Tools.out("saveAll", messageUserRepository.saveAll(mans));
        Pageable pageable = PageRequest.of(0, size);
        Tools.formatOut(messageUserRepository.findAll(pageable).getContent());
        Tools.out(messageUserRepository.findById(new MessageUserPK().setId("1:all").setMsgId("msgid")));
//        Tools.out(manRepository.getOne("1")); //特殊功能?

    }




}