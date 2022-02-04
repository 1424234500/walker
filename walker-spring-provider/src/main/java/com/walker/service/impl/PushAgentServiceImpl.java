package com.walker.service.impl;

import com.walker.config.Context;
import com.walker.dao.JdbcTemplateDao;
import com.walker.mode.sys.PushBindModel;
import com.walker.mode.sys.PushModel;
import com.walker.service.PushAgentService;
import com.walker.service.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service("pushAgentService")
//@Scope("prototype")	//默认单例 dubbo不能单例
public class PushAgentServiceImpl implements PushAgentService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplateDao jdbcTemplateDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    @Qualifier("pushServiceJpush")
    private PushService pushServiceJpush;

    @Autowired
    @Qualifier("pushServiceHw")
    private PushService pushServiceHw;

    /**
     * @param pushModel
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
    public Integer push(PushModel pushModel) {
        String userIds = pushModel.getUSER_ID();
        if (userIds == null || userIds.length() == 0) {
            log.error("参数无效" + pushModel);
            return -1;
        }
        Map<String, Set<String>> typePushIds = new LinkedHashMap<>();
        int res = 0;
        for (String userId : userIds.split(",")) {
            List<PushBindModel> pushBindModels = findBind(userId);
            if (pushBindModels == null || pushBindModels.size() == 0) {
                log.warn("push user have no token? " + userId);
                pushBindModels = new ArrayList<>();
            }
            for (PushBindModel pushBindModel : pushBindModels) {
                Set<String> pushIds = typePushIds.get(pushBindModel.getTYPE());
                if (pushIds == null) {
                    pushIds = new LinkedHashSet<>();
                    typePushIds.put(pushBindModel.getTYPE(), pushIds);
                }
                pushIds.add(pushBindModel.getPUSH_ID());
            }
            res += pushBindModels.size();
        }

        pushServiceJpush.push(pushModel, typePushIds.get(pushServiceJpush.getType()));
        pushServiceHw.push(pushModel, typePushIds.get(pushServiceHw.getType()));


        return res;
    }


    /**
     * 绑定用户id和推送id和推送类别
     * 单用户可注册到多个设备
     * 单设备只能注册单个用户
     * <p>
     * 绑定空pushId则为取消该设备的绑定
     * <p>
     * redis	hash
     * key_${user_id1}
     * ${device_id1}	:	${push_id1}, ${type1}, ${device_id1}
     * ${device_id2}	:	${push_id2}, ${type2}, ${device_id1}
     * <p>
     * redis	hash
     * <p>
     * key1
     * ${device_id1}	:	${user_id1}
     * ${device_id2}	:	${user_id1}
     *
     * @param pushBindModel
     */
    @Override
    public List<PushBindModel> bind(PushBindModel pushBindModel) {
        HashOperations<String, String, PushBindModel> setOperationsUserPush = redisTemplate.opsForHash();
        HashOperations<String, String, String> setOperationsDeviceUser = redisTemplate.opsForHash();
        String userKey = Context.getRedisKeyUserPush() + pushBindModel.getUSER_ID();
        String deviceKey = Context.getRedisKeyDeviceUser();

        pushBindModel.setID("");
        pushBindModel.setS_ATIME("");
        pushBindModel.setS_FLAG("");
        pushBindModel.setS_MTIME("");

//		取消该设备上一次绑定的用户
        String lastUserId = setOperationsDeviceUser.get(deviceKey, pushBindModel.getDEVICE_ID());
        if (lastUserId != null && lastUserId.length() > 0) {
            String userKeyLast = Context.getRedisKeyUserPush() + lastUserId;
            setOperationsDeviceUser.delete(userKeyLast, pushBindModel.getDEVICE_ID());
            long delsize = setOperationsUserPush.delete(userKeyLast, pushBindModel.getDEVICE_ID());
//			if(delsize > 0)
            log.info("push bind 设备" + pushBindModel.getDEVICE_ID() + " 取消旧绑定用户 " + lastUserId + " " + delsize + " " + pushBindModel);
        }
        if (pushBindModel.getPUSH_ID() != null && pushBindModel.getPUSH_ID().length() > 0) {
            setOperationsUserPush.put(userKey, pushBindModel.getDEVICE_ID(), pushBindModel);
            setOperationsDeviceUser.put(deviceKey, pushBindModel.getDEVICE_ID(), pushBindModel.getUSER_ID());
            log.info("push bind 设备" + pushBindModel.getDEVICE_ID() + " 帐号 " + pushBindModel.getUSER_ID() + " 绑定 " + pushBindModel);
        } else {
            log.info("push bind 设备" + pushBindModel.getDEVICE_ID() + " 帐号 " + pushBindModel.getUSER_ID() + " push_id 为空 不做绑定 " + pushBindModel);
        }

        List<PushBindModel> res = setOperationsUserPush.values(userKey);
        return res;
    }

    /**
     * 查找用户绑定列表
     *
     * @param userId
     */
    @Override
    public List<PushBindModel> findBind(String userId) {
        HashOperations<String, String, PushBindModel> setOperationsUserPush = redisTemplate.opsForHash();
        HashOperations<String, String, String> setOperationsDeviceUser = redisTemplate.opsForHash();
        List<PushBindModel> res = new ArrayList<>();
        String deviceKey = Context.getRedisKeyDeviceUser();

        if (userId != null && userId.length() > 0) {
            String userKey = Context.getRedisKeyUserPush() + userId;

            List<PushBindModel> one = setOperationsUserPush.values(userKey);
            if (one != null && one.size() > 0) {
                res.addAll(one);
            }
        } else {
            Set<String> keys = redisTemplate.keys(Context.getRedisKeyUserPush() + "*");
            for (String userKey : keys) {
                List<PushBindModel> one = setOperationsUserPush.values(userKey);
                if (one != null && one.size() > 0) {
                    res.addAll(one);
                }
            }

        }

        return res;
    }

    /**
     * 取消绑定用户id和推送id和推送类别
     * user_id, device_id
     *
     * @param pushBindModels
     */
    @Override
    public List<PushBindModel> unbind(List<PushBindModel> pushBindModels) {
        HashOperations<String, String, PushBindModel> setOperationsUserPush = redisTemplate.opsForHash();
        HashOperations<String, String, String> setOperationsDeviceUser = redisTemplate.opsForHash();
        String deviceKey = Context.getRedisKeyDeviceUser();

        List<PushBindModel> res = new ArrayList<>();
        for (PushBindModel pushBindModel : pushBindModels) {
            String userKey = Context.getRedisKeyUserPush() + pushBindModel.getUSER_ID();

            pushBindModel.setID("");
            pushBindModel.setS_ATIME("");
            pushBindModel.setS_FLAG("");
            pushBindModel.setS_MTIME("");
            if (setOperationsUserPush.hasKey(userKey, pushBindModel.getDEVICE_ID())) {
                setOperationsUserPush.delete(userKey, pushBindModel.getDEVICE_ID());
                res.add(pushBindModel);
            }
            setOperationsDeviceUser.delete(deviceKey, pushBindModel.getDEVICE_ID());
        }
        return res;
    }

    /**
     * 取消绑定用户id和推送id和推送类别
     *
     * @param userId
     */
    @Override
    public List<PushBindModel> unbind(String userId) {

        HashOperations<String, String, PushBindModel> setOperationsUserPush = redisTemplate.opsForHash();
        HashOperations<String, String, String> setOperationsDeviceUser = redisTemplate.opsForHash();
        String deviceKey = Context.getRedisKeyDeviceUser();
        String userKey = Context.getRedisKeyUserPush() + userId;

        List<PushBindModel> res = setOperationsUserPush.values(userKey);

        redisTemplate.delete(userKey);

        return res;
    }


}