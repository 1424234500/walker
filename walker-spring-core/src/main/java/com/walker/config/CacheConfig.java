package com.walker.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walker.service.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * redis缓存的有效时间单位是秒 默认过期时间：1 hours
     */
    @Value("${spring.redis.default.expiration:600}")
    private long redisDefaultExpiration;

    /**
     * 重写Redis序列化方式，使用Json方式:
     * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的。RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer。
     * Spring Data JPA为我们提供了下面的Serializer：
     * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer。
     * 在此我们将自己配置RedisTemplate并定义Serializer。
     *
     * @param redisConnectionFactory
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

    /**
     @Cacheable 注解会先查询是否已经有缓存，有会使用缓存，没有则会执行方法并缓存。
     @Cacheable(value = "emp" ,key = "targetClass + methodName +#p0")
     public List<NewJob> queryAll(mode uid) {
     return newJobDao.findAllByUid(uid);
     }
     此处的value是必需的，它指定了你的缓存存放在哪块命名空间。
     此处的key是使用的spEL表达式，参考上章。这里有一个小坑，如果你把methodName换成method运行会报错，
     观察它们的返回类型，原因在于methodName是String而methoh是Method。
     此处的User实体类一定要实现序列化
     public class mode implements Serializable
     ，否则会报java.io.NotSerializableException异常。
     */
    /**
     * 重写cache实现redis 序列化方式
     *
     * @param redisConnectionFactory
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info(com.walker.service.Config.getPre() + "redisTemplate ");

        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 设置值（value）的序列化采用Jackson2JsonRedisSerializer。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
//        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    /**
     * 通过CacheManager设置缓存的有效时间
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        log.info(com.walker.service.Config.getPre() + "cacheManager " + redisDefaultExpiration);

//        RedisCacheManager cacheManager = RedisCacheManager.create(factory);
//        return cacheManager;
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置CacheManager的值序列化方式为JdkSerializationRedisSerializer,但其实RedisCacheConfiguration默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value,所以以下注释代码为默认实现
        //ClassLoader loader = this.getClass().getClassLoader();
        //JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader);
        //RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer);
        //RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        //设置默认超过期时间是30秒
        defaultCacheConfig.entryTtl(Duration.ofMillis(redisDefaultExpiration));
        //初始化RedisCacheManager
        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
        return cacheManager;

    }


    /**
     * 缓存对象集合中，缓存是以 key-value 形式保存的。当不指定缓存的 key 时，SpringBoot 会使用 SimpleKeyGenerator 生成 key。
     */
    @Bean
    public KeyGenerator keyGenerator() {
        log.debug(Config.getPre() + "keyGenerator");
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
//                String[] ss = method.toString().split(" ");
//                sb.append(ss[ss.length - 1]);
                sb.append(Arrays.toString(params));
                return sb.toString();
            }
        };

    }

}
