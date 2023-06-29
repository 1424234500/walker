package com.walker.service.impl;

import com.walker.core.mode.Bean;
import com.walker.core.util.TimeUtil;
import com.walker.core.util.Tools;
import com.walker.core.util.Watch;
import com.walker.service.MessageService;
import com.walker.socket.model.Msg;
import com.walker.socket.model.UserSocket;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class MessageServiceShardingImplTest {
    @Autowired
    @Qualifier("messageServiceSharding")
    MessageService messageServiceSharding;


    @Test
    public void test(){

//            messageServiceShardingImpl messageServiceSharding = new messageServiceShardingImpl();

            int saveCount = 3;
            int size = 3;
            Watch w = new Watch("test merge and no merge", size);

            w.costln("sizeMsg", messageServiceSharding.sizeMsg());
            w.costln("sizeMsgUser", messageServiceSharding.sizeMsgUser());


            String id = "000";
            String id1 = "001";
            String id2 = "002";
            String id3 = "003";
            String id4 = "004";
            List<String> scores = new ArrayList<>();
            for(int i = 0; i < saveCount; i++) {
                Msg msg = new Msg();
                msg.setPlugin("TEST_" + getClass().getSimpleName());
                msg.setToUserId(Arrays.asList(id1, id2, id3, id4));
                msg.setData(new Bean().set("count", i));
                msg.setFromUser(new UserSocket().setId(id).setName("name"));
                msg.setTimeServerDo(System.currentTimeMillis());


                Long score = messageServiceSharding.save(msg.getToUserId(), msg);
                String t = TimeUtil.getTime(score, "yyyy-MM-dd HH:mm:ss:SSS");
                Tools.out(score, t);
                scores.add(t);
            }
            w.costln("save",saveCount);
            w.costln("sizeMsg", messageServiceSharding.sizeMsg());
            w.costln("sizeMsgUser", messageServiceSharding.sizeMsgUser());

            Tools.formatOut(scores);
            //查接收者的离线消息
            List<Msg> list = messageServiceSharding.findAfter(id1, scores.get(0), 20);
            Tools.formatOut(list);

            //查发送者和接受者会话的历史消息
            List<Msg> list1 = messageServiceSharding.findBefore(id, id1, scores.get(scores.size() - 1), 20);
            Tools.formatOut(list1);

            for(int i = 0;i < size; i++) {
//                Tools.out(i);
                messageServiceSharding.findBefore(id, id1, scores.get(scores.size() - 1), i % 20);
            }
            w.costln("findBefore",size);
            for(int i = 0;i < size; i++) {
//                Tools.out(i);
                messageServiceSharding.findBeforeByMerge(id, id1, scores.get(scores.size() - 1), i % 20);
            }
            w.costln("findBeforeByMerge",size);
            w.res();
            Tools.out(w);
    }
}