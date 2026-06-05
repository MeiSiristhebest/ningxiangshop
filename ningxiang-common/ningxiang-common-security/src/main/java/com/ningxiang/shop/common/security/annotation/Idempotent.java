package com.ningxiang.shop.common.security.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 接口防重幂等注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等 Key，支持 Spring EL 表达式
     */
    String key();

    /**
     * Key 前缀
     */
    String prefix() default "idempotent:";

    /**
     * 过期时间数值
     */
    long expireTime() default 60;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示信息
     */
    String message() default "请勿重复提交请求";
}
