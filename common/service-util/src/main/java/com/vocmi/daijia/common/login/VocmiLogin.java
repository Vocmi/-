package com.vocmi.daijia.common.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author vocmi
 * @Email 2686782542@qq.com
 * @Date 2024-08-14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VocmiLogin {
}
