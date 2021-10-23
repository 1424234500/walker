package com.walker.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WalkerJdbcQuery {
	String value() default "";

	String countQuery() default "";

	String countProjection() default "";

	boolean nativeQuery() default false;

	String name() default "";

	String countName() default "";
}

