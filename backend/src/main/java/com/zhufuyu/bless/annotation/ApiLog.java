package com.zhufuyu.bless.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API日志记录注解
 * 用于标记需要记录访问日志的Controller方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLog {
    
    /**
     * 接口描述
     */
    String value() default "";
    
    /**
     * 是否记录请求参数
     */
    boolean logRequest() default true;
    
    /**
     * 是否记录响应内容
     */
    boolean logResponse() default true;
}
