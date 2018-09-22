package com.aofei.log.annotation;

import java.lang.annotation.*;

/**
 *
 *
 * @auther Tony
 * @create 2018-09-12 15:25
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**模块*/
    String module() default "";

    /**描述*/
    String description() default "";
}
