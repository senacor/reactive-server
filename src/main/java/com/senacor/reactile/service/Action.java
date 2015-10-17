package com.senacor.reactile.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    static String METHOD_NAME = "UseMethodName";
    String value() default METHOD_NAME;
}
