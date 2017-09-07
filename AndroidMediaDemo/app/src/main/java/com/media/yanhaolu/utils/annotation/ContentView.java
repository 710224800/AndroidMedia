package com.media.yanhaolu.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yanhaolu on 2017/8/25.
 */
//运行环境,此处是运行时
@Retention(RetentionPolicy.RUNTIME)
// 此处表示注解可以存放的位置，此处设置为在类上
@Target(ElementType.TYPE)
public @interface ContentView {
    int value();
}
