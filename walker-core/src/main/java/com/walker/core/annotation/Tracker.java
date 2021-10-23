package com.walker.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;

/**
 * 绑定结构 注解和处理器 传递!
 */
public class Tracker {
    Class<? extends Annotation> cls; // 不泛型 ? extends Annotation 上转差别?
    OnAnnotation fun; // 回调处理
    ElementType type;    //Field Method Class

    public Tracker(Class<? extends Annotation> cls, ElementType type, OnAnnotation fun) {
        this.cls = cls;
        this.type = type;
        this.fun = fun;
    }
}