package com.butterknife.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author      :ljb
 * Date        :2018/7/19
 * Description : 用来注入View
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS) // 编译时注解
public @interface BindView {
    int value();
}
