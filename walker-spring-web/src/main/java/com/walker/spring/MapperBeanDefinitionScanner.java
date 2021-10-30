package com.walker.spring;

import com.walker.core.database.BaseDao;
import com.walker.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

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
 * 扫描注册器
 */
class MapperBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
    private Logger log = LoggerFactory.getLogger(getClass());

    private boolean addToConfig = true;

//        private SqlSessionFactory sqlSessionFactory;
//
//        private SqlSessionTemplate sqlSessionTemplate;

//        private String sqlSessionTemplateBeanName;
//
//        private String sqlSessionFactoryBeanName;

    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;

    private WalkerJdbcFactoryBean<?> mapperFactoryBean = new WalkerJdbcFactoryBean<Object>();
    private BaseDao baseDao;


    public MapperBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        log.info("new MapperBeanDefinitionScanner " + registry);
    }

    /**
     * 配置目标扫描  过滤掉不要的 要自己定义的注解才通过 过来做处理
     * Configures parent scanner to search for the right interfaces. It can search
     * for all interfaces or just for those that extends a markerInterface or/and
     * those annotated with the annotationClass
     */
    public void registerFilters() {
        log.info("registerFilters");
        boolean acceptAllInterfaces = true;

        // if specified, use the given annotation and / or marker interface
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        // override AssignableTypeFilter to ignore matches on the actual marker interface
        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter(new TypeFilter() {
                @Override
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    return true;
                }
            });
        }

        // exclude package-info.java
        addExcludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            }
        });
    }

    /**
     * 调用父类的扫描
     * Calls the parent search that will search and register all the candidates.
     * Then the registered objects are post processed to set them as
     * MapperFactoryBeans
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        log.info("doScan");
//(1)先根据context:component-scan 中属性的base-package="com.mango.jtt"配置转换为classpath*:com/mango/jtt/**/*.class（默认格式），并扫描对应下的class和jar文件并获取类对应的路径，返回Resources
//(2)根据指定的不扫描包，指定的扫描包配置进行过滤不包含的包对应下的class和jar。
//（3）封装成BeanDefinition放到队列里。
//            拿到spring扫描的我的注解类
//            这里会扫描到很多冲突然后退出？
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No WalkerJdbc mapper was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    /**
     * 挨个处理我定义的注解类
     * 1、让这些接口类可以被MapperFactoryBean动态代理
     * 2、设置对应的SqlSession到被代理的对象中
     * mybatis.ClassPathMapperScanner.processBeanDefinitions
     *
     * @param beanDefinitions
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();

            if (logger.isDebugEnabled()) {
                logger.debug("Creating MapperFactoryBean with name '" + holder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' mapperInterface");
            }

            // the mapper interface is the original class of the bean
            // but, the actual class of the bean is MapperFactoryBean
            //通过泛型factoryBean工厂的不同构造函数来生成不同mapper 接口的动态代理的实例 注册beanDefine

//                取出beanDefination本来扫描关联到的注解接口
//                Class<?> mapper = definition.getBeanClass();    //java.lang.IllegalStateException: Bean class name [com.walker.mapper.StudentWalkerJdbc] has not been resolved into an actual Class
            String beanClassName = definition.getBeanClassName();   //com.walker.mapper.StudentWalkerJdbc
            Class<?> mapper = ClassUtil.loadClass(beanClassName);
//                覆盖设置该类实现为自定义的一个类
            definition.setBeanClass(this.mapperFactoryBean.getClass());
//                选择构造传参 来动态生成对于mapper接口实例
//                definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // issue #59
            definition.getConstructorArgumentValues().addGenericArgumentValue(mapper);
            definition.getConstructorArgumentValues().addGenericArgumentValue(baseDao);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
//                给到时候生成的对象附加属性变量
            definition.getPropertyValues().add("addToConfig", this.addToConfig);
//                definition.getPropertyValues().add("baseDao", new RuntimeBeanReference("baseDao"));


//                boolean explicitFactoryUsed = false;
//                if (StringUtils.hasText(this.sqlSessionFactoryBeanName)) {
//                     设置sqlSessionFactoryBeanName（sqlSessionFactoryRef的值）的运行时bean引用
//                    definition.getPropertyValues().add("sqlSessionFactory", new RuntimeBeanReference(this.sqlSessionFactoryBeanName));
//                    explicitFactoryUsed = true;
//                } else if (this.sqlSessionFactory != null) {
//                    definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
//                    explicitFactoryUsed = true;
//                }
//
//                if (StringUtils.hasText(this.sqlSessionTemplateBeanName)) {
//                    if (explicitFactoryUsed) {
//                        logger.warn("Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
//                    }
//                    definition.getPropertyValues().add("sqlSessionTemplate", new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
//                    explicitFactoryUsed = true;
//                } else if (this.sqlSessionTemplate != null) {
//                    if (explicitFactoryUsed) {
//                        logger.warn("Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
//                    }
//                    definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
//                    explicitFactoryUsed = true;
//                }
//
//                if (!explicitFactoryUsed) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
//                    }
//                    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
//                }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping MapperFactoryBean with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }





    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }


    public MapperBeanDefinitionScanner setBaseDao(BaseDao baseDao) {
        this.baseDao = baseDao;
        return this;
    }
}

