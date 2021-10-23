package com.walker.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBSQLString {

    /**
     * 如果命名 为value 仅此 并且是没有其他需要赋值的 则可以 使用注解 时省略键名 直接写值
     */
    /**
     * 长度
     */
    int value() default 40;

    /**
     * 字段名
     */
    String name() default "";

    /**
     * 约束
     * 嵌套约束 默认值填充
     */
    DBConstraints DBConstraints() default @DBConstraints;


}
