package com.touchspring.ailge.utils;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PropertyMsg {
    String value();
}
