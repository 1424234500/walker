package com.walker.config;

import com.walker.core.mode.Key;
import com.walker.core.mode.school.User;
import com.walker.core.util.TimeUtil;
import com.walker.dao.RedisDao;
import com.walker.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理
 */
@Configuration
@EnableCaching
public class ShiroConfig {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    SecurityManager securityManager;
    @Autowired
    UserService userService;

    @Autowired
    RedisDao redisDao;
    /**
     *  redis缓存的有效时间单位是秒 默认过期时间：1 hours
     */
    @Value("${session.redis.expiration:1800}")
    private int sessionRedisExpiration;



    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /*重要，设置自定义拦截器，当访问某些自定义url时，使用这个filter进行验证*/
//        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
        //如果map里面key值为authc,表示所有名为authc的过滤条件使用这个自定义的filter
        //map里面key值为myFilter,表示所有名为myFilter的过滤条件使用这个自定义的filter，具体见下方
//        filters.set("myFilter", new MyFilter());
//        shiroFilterFactoryBean.setFilters(filters);
        /*---------------------------------------------------*/

        //拦截器
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
        //配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        //　　anon:所有url都都可以匿名访问;
        //　　authc: 需要认证才能进行访问;
        //　　user:配置记住我或认证通过可以访问；
        //放开静态资源的过滤
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        //放开登录url的过滤
        filterChainDefinitionMap.put("/login*", "anon");
        ///////////////////////////////////////////////////////
        //对于指定的url，使用自定义filter进行验证
//        filterChainDefinitionMap.set("/my/**", "myFilter");
        //可以配置多个filter，用逗号分隔，按顺序过滤，下方表示先通过自定义filter的验证，再通过shiro默认过滤器的验证
        //filterChainDefinitionMap.set("/targetUrl", "myFilter,authc");
        ///////////////////////////////////////////////////////
        //过滤链定义，从上向下顺序执行，一般将 /**放在最为下边
        //url从上向下匹配，当条件匹配成功时，就会进入指定filter并return(不会判断后续的条件)，因此这句需要在最下边
        filterChainDefinitionMap.put("/**", "anon");//authc

        //如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login.html");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index.html");
        // 未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }


    @Bean("securityManager")
    public SecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        AuthorizingRealm authorizingRealm = new AuthorizingRealm() {
            //添加角色权限
            @Override
            protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
                //获取用户
                User user = (User) principalCollection.getPrimaryPrincipal();
                //获取权限列表
                List<String> roles = Arrays.asList("role01");
                log.info("doGetAuthorizationInfo role  " + user + " " + roles);

                //添加角色和权限
                SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                roles.forEach(item -> simpleAuthorizationInfo.addRole(item));
                return simpleAuthorizationInfo;
            }

            //登录认证
            @Override
            protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
                UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

                String username = usernamePasswordToken.getUsername();

                User user = new User().setNAME(username);
                log.info("doGetAuthenticationInfo " + user);

                if (user == null) {
                    return null;
                } else {
                    SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user, user.getPWD(), getName());
                    return simpleAuthenticationInfo;
                }
            }
        };
        //设置缓存
        authorizingRealm.setCacheManager(new MemoryConstrainedCacheManager());
        //设置密码校验规则
        authorizingRealm.setCredentialsMatcher(new SimpleCredentialsMatcher() {
            @Override
            public boolean doCredentialsMatch (AuthenticationToken token, AuthenticationInfo info){
                User user = (User) info.getPrincipals().getPrimaryPrincipal(); //这里取出的是我们在认证方法中放入的用户信息也就是我们从数据库查询出的用户信息
                UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;//这里是前端发送的用户名密码信息
                String requestPwd = new String(usernamePasswordToken.getPassword());
                String requestPwdEncryption = getEncryptionPassword(requestPwd, user.getID());//获取加密后的密码
                log.info("SimpleCredentialsMatcher " + user + " pwd " + requestPwdEncryption);
                String dbPwd = user.getPWD();
                return requestPwdEncryption.equals(dbPwd);
            }


        });
        manager.setRealm(authorizingRealm);
        return manager;
    }





    @Value("${password.algorithmName:md5}") //这个可以在application.properites里自行配置 如（md5，sha，base64）等等
    private String algorithmName;
    @Value("${password.hashIterations:1}")//这个是加密的次数
    private int hashIterations;

    /**
     * 返回加密后的密码
     *
     * @param password
     * @param userId
     */
    public String getEncryptionPassword(String password, String userId) {
        //四个参数的讲解 1，加密类型，2，原始密码，3，盐值，可以是固定的，这里我门采用用户的userId作为用户的盐值进行加盐，4，加密的迭代次数
        return new SimpleHash(algorithmName, password, userId, hashIterations).toString();


    }

    /**
     * 获取在线用户
     * @param token
     */
    public User getOnlineUser(String token){
        return redisDao.get(token, null);
    }
    /**
     * 保存在线用户 token 放入redis缓存
     * @param user
     */
    public String onlineUser(User user) {
        user = userService.auth(user);
        if(user != null) {
            String token = Key.getLoginToken(user.getID() + ":" + TimeUtil.getTimeSequence());
            redisDao.set(token, user, sessionRedisExpiration);
            if (Context.getRequest() != null) {
                Context.getRequest().getSession().setAttribute("TOKEN", token);
            }
            return token;
        }else{
            return "";
        }
    }
    /**
     * 刷新在线用户 token 放入redis缓存
     * @param token
     */
    public void keeponUser(String token) {
        redisDao.expire(token, sessionRedisExpiration);
    }



}