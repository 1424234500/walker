package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.mode.PushBindModel;
import com.walker.mode.PushModel;
import com.walker.mode.PushType;
import com.walker.service.PushAgentService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class PushAgentServiceImplTest  extends ApplicationProviderTests {

    @Autowired
    PushAgentService pushAgentService;



    String userId = "userid001";
    String userId1 = "userid002";
    String pushId = "push_id001";
    String deviceId = "device_id001";

    String pushId1 = "push_id002";
    String deviceId1 = "device_id002";
    PushBindModel pushBindModel = new PushBindModel()
            .setPUSH_ID(pushId)
            .setDEVICE_ID(deviceId)
            .setTYPE(PushType.APNS)
            .setUSER_ID(userId)
            ;
    PushBindModel pushBindModel1 = new PushBindModel()
            .setPUSH_ID(pushId1)
            .setDEVICE_ID(deviceId1)
            .setTYPE(PushType.JPUSH)
            .setUSER_ID(userId)
            ;

    PushBindModel pushBindModel2 = new PushBindModel()
            .setPUSH_ID(pushId1)
            .setDEVICE_ID(deviceId1)
            .setTYPE(PushType.JPUSH)
            .setUSER_ID(userId1)
            ;
    PushModel pushModel = new PushModel()
            .setTITLE("title")
            .setCONTENT("content")
            .setUSER_ID(userId + "," + userId1)
            ;

    @Test
    public void push() {
        pushAgentService.unbind(userId);
        pushAgentService.unbind(userId1);
        pushAgentService.bind(pushBindModel);
        pushAgentService.bind(pushBindModel1);
        pushAgentService.bind(pushBindModel2);
        out("push", pushAgentService.push(pushModel));
        out(userId, pushAgentService.findBind(userId));
        out(userId1, pushAgentService.findBind(userId1));


    }

    @Test
    public void bind() {
        pushAgentService.unbind(userId);
        pushAgentService.bind(pushBindModel);
        List<PushBindModel> list = pushAgentService.findBind(userId);
        out(list);
        Assert.assertTrue(list.size() == 1 && list.get(0).getPUSH_ID().equalsIgnoreCase(pushId));
    }

    @Test
    public void findBind() {
        pushAgentService.unbind(userId);
        pushAgentService.bind(pushBindModel);
        pushAgentService.bind(pushBindModel1);
        List<PushBindModel> list = pushAgentService.findBind(userId);
        out(list);

        List<PushBindModel> list1 = pushAgentService.findBind("");
        out(list);
        Assert.assertTrue(list.size() == 2 );

    }

    @Test
    public void unbind() {
        pushAgentService.unbind(userId);
        pushAgentService.bind(pushBindModel);
        pushAgentService.bind(pushBindModel1);
        pushAgentService.unbind(userId);
        List<PushBindModel> list = pushAgentService.findBind(userId);
        out(list);
        Assert.assertTrue(list.size() == 0 );

    }


    @Test
    public void unbind1() {

        pushAgentService.unbind(userId);
        pushAgentService.bind(pushBindModel);
        pushAgentService.bind(pushBindModel1);
        pushAgentService.unbind(Arrays.asList(pushBindModel1));
        List<PushBindModel> list = pushAgentService.findBind(userId);
        out(list);
        Assert.assertTrue(list.size() == 1 && list.get(0).getPUSH_ID().equalsIgnoreCase(pushId));

    }
}