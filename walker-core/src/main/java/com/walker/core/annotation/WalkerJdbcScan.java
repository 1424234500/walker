package com.walker.core.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
//@Import(MapperScannerRegistrar.class)	//由spring项目启动类导入
public @interface WalkerJdbcScan {
    /**
     * 基本的扫描包路径
     * Alias for the {@link #value()} attribute. Allows for more concise
     * annotation declarations e.g.:
     * {@code @EnableMyBatisMapperScanner("org.my.pkg")} instead of {@code
     *
     * @EnableMyBatisMapperScanner(basePackages= "org.my.pkg"})}.
     */
    String[] value() default {"com.walker"};


    /**
     * Base packages to scan for MyBatis interfaces. Note that only interfaces
     * with at least one method will be registered; concrete classes will be
     * ignored.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages
     * to scan for annotated components. The package of each class specified will be scanned.
     * <p>Consider creating a special no-op marker class or interface in each package
     * that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

//    可配置bean name生成器
//    /**
//     * The {@link BeanNameGenerator} class to be used for naming detected components
//     * within the Spring container.
//     */
//    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    /**
     * 需要扫描处理的注解
     * This property specifies the annotation that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have
     * the specified annotation.
     * <p>
     * Note this can be combined with markerInterface.
     */
    Class<? extends Annotation> annotationClass() default WalkerJdbc.class;

    /**
     * This property specifies the parent that the scanner will search for.
     * <p>
     * The scanner will register all interfaces in the base package that also have
     * the specified interface class as a parent.
     * <p>
     * Note this can be combined with annotationClass.
     */
    Class<?> markerInterface() default Class.class;


//    /**
//     * Specifies which {@code SqlSessionTemplate} to use in the case that there is
//     * more than one in the spring context. Usually this is only needed when you
//     * have more than one datasource.
//     */
//    String sqlSessionTemplateRef() default "";
//
//    /**
//     * Specifies which {@code SqlSessionFactory} to use in the case that there is
//     * more than one in the spring context. Usually this is only needed when you
//     * have more than one datasource.
//     */
//    String sqlSessionFactoryRef() default "";


//    选择写死factoryBean
//    /**
//     * Specifies a custom MapperFactoryBean to return a mybatis proxy as spring bean.
//     *
//     */
//    Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;
}
