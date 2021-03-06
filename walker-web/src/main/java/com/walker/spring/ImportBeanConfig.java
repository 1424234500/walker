package com.walker.spring;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walker.core.annotation.*;
import com.walker.dao.JdbcDao;
import com.walker.mapper.StudentWalkerJdbc;
import com.walker.mode.Student;
import com.walker.service.Config;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

/**
 * spring?????? ???????????????beanDefin
 *
 * ??????mybatis MapperScannerRegistrar
 * MybatisAutoConfiguration
 *     @Configuration
 *     @Import({MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class})
 *     @ConditionalOnMissingBean({MapperFactoryBean.class})
 * ????????????
 * ????????????
 * ??????
 *
 * xml??????bean
 * @Bean ?????????
 * ????????????????????? @Import ??????????????????
 *
 *
 * @Import ???????????????????????????component??? ???????????????@Compoent??????@Configuration???????????? ?????????BeanFactory??????????????????getBean?????????
 * ??????????????? ImportSelector ?????? ImportBeanDefinitionRegistrar ??????????????????
 * ??????spring???????????????????????????beanFactory???????????????????????????????????????
 *
 * ImportBeanDefinitionRegistrar????????????????????????import????????????????????????????????????????????????????????????
 *
 */
public class ImportBeanConfig implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * ????????????spring???jdbc??????dao??????????????????new???????????????jdbc???
     */
    @Autowired
    JdbcDao jdbcDao;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.info(Config.getPre() + " ImportBeanConfig ");
        List<String> basePackages = new ArrayList<String>();
        MapperBeanDefinitionScanner scanner = new MapperBeanDefinitionScanner(registry).setBaseDao(jdbcDao);

//        ??????????????????????????????????????????map
        Map map = importingClassMetadata.getAnnotationAttributes(WalkerJdbcScan.class.getName());
//        Map map1 = importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName());
        if(map == null){
            log.error("???????????????????????? ?????? WalkerJdbcScan ?????????????????????");
            throw new NullPointerException("???????????????????????? ?????? WalkerJdbcScan ????????????????????? ");
        }else{
//            ????????????scan??????????????????????????????
            AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(map);
    //        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            // this check is needed in Spring 3.1
            if (resourceLoader != null) {
                scanner.setResourceLoader(resourceLoader);
            }

//            ??????????????????????????????
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

//        WalkerJdbc ??????mybatis?????? ???????????? ????????????
//        ????????????
//        Tracker[] trackerClass = {
//            new Tracker(WalkerJdbc.class, ElementType.TYPE,  new OnAnnotation(){
//                @Override
//                public Status make(Annotation annotation, ElementType type, Object object, Class<?> cls) {
//                    if(type.equals(ElementType.TYPE)){//???
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
////                new Tracker(Test.class, ElementType.METHOD,  new TestTracker()),
//        };
//
//        Tracker[][] trackerAll = { trackerClass, trackerField, trackerMethod }; //??????
//        TrackerUtil.make("", trackerAll);


    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    /**
     * ???????????????????????????
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


