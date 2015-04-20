package com.senacor.reactile.hystrix.interception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to intercept a Servicecall with a HystrixCommand
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 17.04.15
 * Time: 09:01
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HystrixCmd {

    /**
     * @return The Hystrix Command, which should be used to intercept the annotates method.
     * Use the InterceptableHystrixObservableCommand directly, if the method parameters of the interceptet method matches with the Hystrix Command Constructor Parameter.
     * Otherwise use a Factory with a matching method.
     * <p>
     * If "null" (Void.class ~ null), we will use a convention to search a matching Hystrix Command.
     * <p>
     * The annotated Method must return a rx.Observable
     */
    Class value() default Void.class;
}
