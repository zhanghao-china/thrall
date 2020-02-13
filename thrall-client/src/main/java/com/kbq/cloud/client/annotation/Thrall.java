package com.kbq.cloud.client.annotation;

import com.kbq.cloud.common.ThrallType;


import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface Thrall {

    String value() default ThrallType.SYSTEM_EXCEPTION;

}
