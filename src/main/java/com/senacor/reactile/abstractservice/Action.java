package com.senacor.reactile.abstractservice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    public enum MessagePattern {
        PublishSubsrcribe, RequestResponse, RequestSubscribe;
    }

    static String METHOD_NAME = "UseMethodName";
    String name() default METHOD_NAME;
    MessagePattern pattern() default MessagePattern.RequestResponse;
    Class<?> returnType();
}
