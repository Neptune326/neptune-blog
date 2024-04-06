package com.neptune.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BackLog {

    String module() default "";

    String type() default "";

    boolean param() default true;

    boolean result() default true;

}
