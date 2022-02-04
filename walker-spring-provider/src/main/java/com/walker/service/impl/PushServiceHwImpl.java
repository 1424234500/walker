package com.walker.service.impl;

import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import com.walker.dao.JdbcTemplateDao;
import com.walker.mode.sys.PushModel;
import com.walker.mode.sys.PushType;
import com.walker.service.PushAgentService;
import com.walker.service.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Service("pushServiceHw")
//@Scope("prototype")	//默认单例 dubbo不能单例
public class PushServiceHwImpl implements PushService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Cache<String> cache = ConfigMgr.getInstance();
    @Autowired
    private JdbcTemplateDao jdbcTemplateDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PushAgentService pushAgentService;

    @Override
    public String getType() {
        return PushType.HW;
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
    public Integer push(PushModel pushBindModel, Set<String> pushIds) {


        return null;
    }
}