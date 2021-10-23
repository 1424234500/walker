package com.walker.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;


public interface OnAnnotation {
    Status make(Annotation annotation, ElementType type, Object object, Class<?> cls);
}
