package com.walker.service.impl;

import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import com.walker.core.encode.MD5;
import com.walker.dao.JdbcTemplateDao;
import com.walker.service.LoginService;
import com.walker.util.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Transactional
@Service("loginService")
//@Scope("prototype") 
public class LoginServiceImpl implements LoginService, Serializable {
    private static final long serialVersionUID = 8304941820771045214L;
    private final Cache<String> cache = ConfigMgr.getInstance();
    @Autowired
    private JdbcTemplateDao JdbcTemplateDao;

    @Override
    public Boolean login() {
        return saveLogin("TEST", "");
    }

    @Override
    public Boolean saveLogin(String id, String pwd) {
        String token = MD5.makeStr(id + System.currentTimeMillis());
        Map map = cache.get(CACHE_KEY, new LinkedHashMap<String, Object>());
        Bean bean = new Bean().put("TOKEN", token).put("ID", id).put("TIME", System.currentTimeMillis()).put("EXPIRE", 60L * 1000);
        map.put(token, bean);
        cache.put(CACHE_KEY, map);
        log.info("登录" + id + "." + pwd + "." + token);
//		Context.setToken(token);
//		Context.getRequest().getSession().setAttribute("TOKEN", token);
        return true;
    }


}