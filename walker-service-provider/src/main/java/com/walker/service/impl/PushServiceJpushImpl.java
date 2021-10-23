package com.walker.service.impl;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.*;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.walker.config.MakeConfig;
import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import com.walker.dao.ConfigDao;
import com.walker.dao.JdbcTemplateDao;
import com.walker.mode.PushModel;
import com.walker.mode.PushType;
import com.walker.service.PushAgentService;
import com.walker.service.PushService;
import com.walker.util.Watch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Transactional
@Service("pushServiceJpush")
//@Scope("prototype")	//默认单例 dubbo不能单例
public class PushServiceJpushImpl implements PushService {
    static String ALERT = "alert";
    static String MSG_CONTENT = "msg_content";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Cache<String> cache = ConfigMgr.getInstance();
    @Autowired
    ConfigDao configDao;
    @Autowired
    private MakeConfig makeConfig;
    @Autowired
    private JdbcTemplateDao jdbcTemplateDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PushAgentService pushAgentService;

    public static PushPayload buildPushPayLoad(String[] registrationIds, String title, String content, Map<String, String> ext) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(registrationIds))
                .setNotification(Notification.android(content, title, ext))
                .build();
    }

    //	快捷地构建推送对象：所有平台，所有设备，内容为 ALERT 的通知。
    public static PushPayload buildPushObject_all_all_alert(String alert) {
        return PushPayload.alertAll(alert);
    }

    //	构建推送对象：所有平台，推送目标是别名为 "alias1"，通知内容为 ALERT。
    public static PushPayload buildPushObject_all_alias_alert(String alias, String alert) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.alert(alert))
                .build();
    }

    //	构建推送对象：平台是 Android，目标是 tag 为 "tag1" 的设备，内容是 Android 通知 ALERT，并且标题为 TITLE。
    public static PushPayload buildPushObject_android_tag_alertWithTitle(String tag, String alert, String title) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.android(alert, title, null))
                .build();
    }

    //	构建推送对象：平台是 iOS，推送目标是 "tag1", "tag_all" 的交集，推送内容同时包括通知与消息 - 通知信息是 ALERT，角标数字为 5，通知声音为 "happy"，并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
    public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag_and("tag1", "tag_all"))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(ALERT)
                                .setBadge(5)
                                .setSound("happy")
                                .addExtra("from", "JPush")
                                .build())
                        .build())
                .setMessage(Message.content(MSG_CONTENT))
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .build())
                .build();
    }

    //	构建推送对象：平台是 Andorid 与 iOS，推送目标是 （"tag1" 与 "tag2" 的并集）交（"alias1" 与 "alias2" 的并集），推送内容是 - 内容为 MSG_CONTENT 的消息，并且附加字段 from = JPush。
    public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))
                        .addAudienceTarget(AudienceTarget.alias("alias1", "alias2"))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(MSG_CONTENT)
                        .addExtra("from", "JPush")
                        .build())
                .build();
    }

    @Override
    public String getType() {
        return PushType.JPUSH;
    }

    /**
     * @Column(name = "LEVEL", columnDefinition = "varchar(32) default '0' comment '优先级' ")    //255
     * private String LEVEL;
     * @Column(name = "USER_ID", columnDefinition = "varchar(32) default '' comment '目标用户id' ")    //255
     * private String USER_ID;
     * @Column(name = "TITLE", columnDefinition = "varchar(512) default 'title' comment '标题' ")    //255
     * private String TITLE;
     * @Column(name = "CONTENT", columnDefinition = "varchar(512) default 'content' comment '内容' ")    //255
     * private String CONTENT;
     * @Column(name = "TYPE", columnDefinition = "varchar(512) default '0' comment '类别 提醒|透传' ")    //255
     * private String TYPE;
     * @Column(name = "EXT", columnDefinition = "varchar(998) default '' comment '扩展参数' ")    //255
     * private String EXT;
     * <p>
     * 推送 是否需要队列缓冲功能 优先级功能?
     */
    @Override
    public Integer push(PushModel pushModel, Set<String> pushIds) {
        if (pushModel == null || pushIds == null || pushIds.size() == 0) {
            return -2;
        }

        String title = pushModel.getTITLE();
        String content = pushModel.getCONTENT();
        Map<String, String> ext = pushModel.getEXTObj();

        JPushClient jpushClient = getJPushClient();
        int res = 0;
        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushPayLoad(pushIds.toArray(new String[0]), title, content, ext);
        Watch w = new Watch("push " + getType(), pushModel).putln(String.valueOf(pushIds)).putln(String.valueOf(payload));
        try {
            PushResult result = jpushClient.sendPush(payload);
            jpushClient.close();
            w.put(result.getResponseCode());
            res = result.getResponseCode();
            w.resSlf4j(result, log);
        } catch (Exception e) {
            log.error(e.toString(), e);
            w.exceptionWithThrow(e);
        } finally {
//			log.info(w.toString());
        }

        return res;
    }

    public JPushClient getJPushClient() {
//		push.jpush.APP_KEY=557bf3f8c230ec7cdefb0e06
//		push.jpush.MASTER_SECRET=a5dba9ffe772656db0e7149d
//		log.debug("getJPushClient " + configDao.get("com.walker.service.impl.PushServiceJpushImpl.secret", "") + " " + configDao.get("com.walker.service.impl.PushServiceJpushImpl.appkey", ""));
        return new JPushClient(configDao.get("com.walker.service.impl.PushServiceJpushImpl.secret", "a5dba9ffe772656db0e7149d"), configDao.get("com.walker.service.impl.PushServiceJpushImpl.appkey", "a5dba9ffe772656db0e7149d"), null, ClientConfig.getInstance());
    }

    //	构建推送对象：推送内容包含SMS信息
    public void testSendWithSMS() {
        JPushClient jpushClient = getJPushClient();
        try {
            SMS sms = SMS.newBuilder()
                    .setDelayTime(1000)
                    .setTempID(2000)
                    .addPara("TestModel", 1)
                    .build();
            PushResult result = jpushClient.sendAndroidMessageWithAlias("TestModel SMS", "test sms", sms, "alias1");
            log.info("Got result - " + result);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
        }
    }

//	Tag/Alias 样例
//	以下片断来自项目代码里的文件：example / cn.jpush.api.examples.DeviceExample
//
////	获取Tag Alias
//    try {
//		TagAliasResult result = jpushClient.getDeviceTagAlias(REGISTRATION_ID1);
//
//		log.info(result.alias);
//		log.info(result.tags.toString());
//	} catch (APIConnectionException e) {
//		log.error("Connection error. Should retry later. ", e);
//	} catch (APIRequestException e) {
//		log.error("Error response from JPush server. Should review and fix it. ", e);
//		log.info("HTTP Status: " + e.getStatus());
//		log.info("Error Code: " + e.getErrorCode());
//		log.info("Error Message: " + e.getErrorMessage());
//	}
////	绑定手机号
//    try {
//		DefaultResult result =  jpushClient.bindMobile(REGISTRATION_ID1, "13000000000");
//		log.info("Got result " + result);
//	} catch (APIConnectionException e) {
//		log.error("Connection error. Should retry later. ", e);
//	} catch (APIRequestException e) {
//		log.error("Error response from JPush server. Should review and fix it. ", e);
//		log.info("HTTP Status: " + e.getStatus());
//		log.info("Error Code: " + e.getErrorCode());
//		log.info("Error Message: " + e.getErrorMessage());
//	}

}