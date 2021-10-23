package com.walker.spring;

import com.walker.core.annotation.WalkerJdbcFactory;
import com.walker.core.database.BaseDao;
import com.walker.core.database.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * spring扩展 自定义导入beanDefin
 *
 * 模拟mybatis MapperScannerRegistrar
 * MybatisAutoConfiguration
 *     @Configuration
 *     @Import({MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class})
 *     @ConditionalOnMissingBean({MapperFactoryBean.class})
 * 扫描注解
 * 动态代理
 * 反射
 *
 * xml配置bean
 * @Bean 被扫描
 * 其他被扫描的类 @Import 导入该配置类
 *
 *
 * @Import 引入的是一个正常的component， 那么会作为@Compoent或者@Configuration来处理， 这样在BeanFactory里边可以通过getBean拿到，
 * 但如果你是 ImportSelector 或者 ImportBeanDefinitionRegistrar 接口的实现，
 * 那么spring并不会将他们注册到beanFactory中，而只是调用他们的方法。
 *
 * ImportBeanDefinitionRegistrar只能通过由其它类import的方式来加载，通常是主启动类类或者注解。
 *
 */

/**
 * MapperFactoryBean 动态接口对象生成器
 * @param <T>
 */
class WalkerJdbcFactoryBean<T> implements FactoryBean<T> {
    private Logger log = LoggerFactory.getLogger(getClass());


    private Class<T> mapperInterface;
    private BaseDao baseDao = null;

    private boolean addToConfig = true;

    public WalkerJdbcFactoryBean() {
        //intentionally empty
    }

    public WalkerJdbcFactoryBean(Class<T> mapperInterface, BaseDao baseDao) {
        this.baseDao = baseDao;
        this.mapperInterface = mapperInterface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getObject() throws Exception {
//        return getSqlSession().getMapper(this.mapperInterface);
//        handler MapperProxy
        if(baseDao == null){
            baseDao = new Dao();
        }
        return WalkerJdbcFactory.getInstance(mapperInterface, baseDao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    //------------- mutators --------------

    /**
     * Sets the mapper interface of the MyBatis mapper
     *
     * @param mapperInterface class of the interface
     */
    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * Return the mapper interface of the MyBatis mapper
     *
     * @return class of the interface
     */
    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    /**
     * If addToConfig is false the mapper will not be added to MyBatis. This means
     * it must have been included in mybatis-config.xml.
     * <p/>
     * If it is true, the mapper will be added to MyBatis in the case it is not already
     * registered.
     * <p/>
     * By default addToCofig is true.
     *
     * @param addToConfig
     */
    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    /**
     * Return the flag for addition into MyBatis config.
     *
     * @return true if the mapper will be added to MyBatis in the case it is not already
     * registered.
     */
    public boolean isAddToConfig() {
        return addToConfig;
    }
}
