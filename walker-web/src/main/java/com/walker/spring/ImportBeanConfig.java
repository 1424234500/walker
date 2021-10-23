package com.walker.spring;

import com.walker.core.annotation.WalkerJdbcFactory;
import com.walker.core.annotation.WalkerJdbcScan;
import com.walker.dao.JdbcDao;
import com.walker.mapper.StudentWalkerJdbc;
import com.walker.service.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class ImportBeanConfig implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 尝试注入spring的jdbc作为dao实现，若无则new一个自定义jdbc？
     */
    @Autowired
    JdbcDao jdbcDao;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.info(Config.getPre() + " ImportBeanConfig ");
        List<String> basePackages = new ArrayList<>();
        MapperBeanDefinitionScanner scanner = new MapperBeanDefinitionScanner(registry).setBaseDao(jdbcDao);

//        获取我的注解的所有属性和值的map
        Map map = importingClassMetadata.getAnnotationAttributes(WalkerJdbcScan.class.getName());
//        Map map1 = importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName());
        if(map == null){
            log.error("没有在启动类入口 配置 WalkerJdbcScan 默认扫描包路径");
            throw new NullPointerException("没有在启动类入口 配置 WalkerJdbcScan 默认扫描包路径 ");
        }else{
//            整理获取scan注解的配置的各类属性
            AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(map);
    //        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            // this check is needed in Spring 3.1
            if (resourceLoader != null) {
                scanner.setResourceLoader(resourceLoader);
            }

//            要处理的注解的白名单
            Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
            if (!Annotation.class.equals(annotationClass)) {
                log.info("annotationClass " + annotationClass.toString());
                scanner.setAnnotationClass(annotationClass);
            }

            Class<?> markerInterface = annoAttrs.getClass("markerInterface");
            if (!Class.class.equals(markerInterface)) {
                log.info("markerInterface " + markerInterface.toString());
                scanner.setMarkerInterface(markerInterface);
            }

//            Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
////            if (!BeanNameGenerator.class.equals(generatorClass)) {
////                log.info("nameGenerator " + generatorClass.toString());
////                scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
////            }

    //        Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
    //        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
    //            scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
    //        }
    //        scanner.setSqlSessionTemplateBeanName(annoAttrs.getString("sqlSessionTemplateRef"));
    //        scanner.setSqlSessionFactoryBeanName(annoAttrs.getString("sqlSessionFactoryRef"));

            for (String pkg : annoAttrs.getStringArray("value")) {
                if (StringUtils.hasText(pkg)) {
                    log.info("value " + pkg);
                    basePackages.add(pkg);
                }
            }
            for (String pkg : annoAttrs.getStringArray("basePackages")) {
                if (StringUtils.hasText(pkg)) {
                    log.info("basePackages " + pkg);
                    basePackages.add(pkg);
                }
            }
            for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
                log.info("basePackageClasses " + ClassUtils.getPackageName(clazz));
                basePackages.add(ClassUtils.getPackageName(clazz));
            }
        }

        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));

//        WalkerJdbc 仿造mybatis实现 代理注入 不可行？
//        扫描注解
//        Tracker[] trackerClass = {
//            new Tracker(WalkerJdbc.class, ElementType.TYPE,  new OnAnnotation(){
//                @Override
//                public Status make(Annotation annotation, ElementType type, Object object, Class<?> cls) {
//                    if(type.equals(ElementType.TYPE)){//类
//
//                        Class<?> mapper = (Class<?>)object;
//                        log.debug("begin import bean " + mapper.toString());
//
//                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
//
//                        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
//                        beanDefinition.setBeanClass(StudentWalkerJdbcFactoryBean.class);
////                        beanDefinition.setBeanClass(WalkerJdbcFactoryBean.class);
////                        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(mapper);
//                        log.debug("beanDefinition  " + beanDefinition.toString());
//
//                        beanDefinitionRegistry.registerBeanDefinition(mapper.getSimpleName(), beanDefinition);
//
//                        log.debug("end   import bean " + mapper.toString());
//
//                        return Status.STOP_CLASS;
//                    }
//
//                    return null;
//                }
//            }),
//        };
//        Tracker[] trackerField = {
//        };
//        Tracker[] trackerMethod = {
////                new Tracker(UseCase.class, ElementType.METHOD,  new UseCaseTracker()),
////                new Tracker(TestModel.class, ElementType.METHOD,  new TestTracker()),
//        };
//
//        Tracker[][] trackerAll = { trackerClass, trackerField, trackerMethod }; //分层
//        TrackerUtil.make("", trackerAll);


    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    /**
     * 指定接口对象生成器
     */
    class StudentWalkerJdbcFactoryBean implements FactoryBean<StudentWalkerJdbc> {

        @Override
        public StudentWalkerJdbc getObject() throws Exception {
            return WalkerJdbcFactory.getInstance(StudentWalkerJdbc.class, jdbcDao);
        }

        @Override
        public Class<?> getObjectType() {
            return StudentWalkerJdbc.class;
        }
    }







}


