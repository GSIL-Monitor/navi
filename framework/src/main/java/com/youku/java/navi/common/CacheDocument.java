package com.youku.java.navi.common;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CacheDocument {

    String key();

    String mq();

    int rate() default 15 * 60000;

    int expire() default 60 * 60 * 24;

}
