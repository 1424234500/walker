package com.walker.core.exception;

import com.walker.mode.Property;
import org.apache.commons.lang3.StringUtils;

public class ExceptionUtil {
    public static void blankThrow(Property<?> model) {
        if(model.getValue() == null){
            throw new RuntimeException(model + " isNull");
        }
        if(model.getValue() instanceof String) {
            if (StringUtils.isBlank((String)model.getValue())) {
                throw new RuntimeException(model + " isBlank");
            }
        }
    }


    public static void on(Exception e) {
        throw new RuntimeException(e);
    }
}
