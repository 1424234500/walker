package com.walker.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解
 * 看起来就像接口的方法, 唯一的区别是你可以为其指定默认值
 *
 * @Target 表示注解可用于什么地方
 * CONSTRUCTOR构造器
 * FIELD域声明包括enum实例
 * METHOD 函数
 * LOCAL_VARIABLE局部变量声明
 * PACKAGE包声明
 * PARAMETER参数声明
 * TYPE类 接口 enum声明
 * 不写可以用于所有ElementType?
 * @Retention 表示需要什么级别保存该注解信息
 * SOURCE注解被编译器丢弃
 * CLASS注解class文件可用 被VM丢弃
 * RUNTIME vm在运行期也保留 可以通过反射获取信息
 * @Documented 将此注解包含在Javadoc中
 * @Inherited 允许子类继承父类中的注解
 * <p>
 * 基本功能测试
 * <p>
 * 方法注解 运行时
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCase {

    int id();

    /**
     * 默认值不能为null 可以是基本类型, class, String, enum, Annotation(嵌套),或以上对应数组 但不能是包装类型?
     */
    String description() default "no description";


}
