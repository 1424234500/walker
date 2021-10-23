package com.walker.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBSQLInteger {

    /**
     * 字段名
     */
    String name() default "";

    /**
     * 嵌套约束 默认值填充
     */
    DBConstraints DBConstraints() default @DBConstraints(allowNull = false);


}
