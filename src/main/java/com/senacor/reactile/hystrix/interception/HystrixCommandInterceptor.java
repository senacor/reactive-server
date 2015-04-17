package com.senacor.reactile.hystrix.interception;

import com.google.inject.Injector;
import com.netflix.hystrix.HystrixObservable;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import rx.Observable;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.isAssignableFrom;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * TODO (ak) Beschreibung
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 17.04.15
 * Time: 11:05
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class HystrixCommandInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HystrixCommandInterceptor.class);

    @Inject
    private Injector injector;

    public HystrixCommandInterceptor() {
        //this.injector = injector;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        logger.info("invoking: " + toSignatureString(invocation));

        // Validation
        isAssignableFrom(Observable.class, invocation.getMethod().getReturnType(),
                "@%s is only allowed on Methods which return %s. Method: %s",
                HystrixCmd.class.getSimpleName(), Observable.class.getName(), toSignatureString(invocation));

        Class commandClass = invocation.getMethod().getAnnotation(HystrixCmd.class).value();
        if (Void.class.equals(commandClass)) {
            logger.info("search for matching HystrixCommand oder HystrixCommandFactory");
            // TODO (ak) impl
            throw new UnsupportedOperationException("not yet implemented");
        } else if (commandClass.isInterface()) {
            logger.info("try to find HystrixCommandFactory in guice...");
            Object hystrixCommandFactory = injector.getInstance(commandClass);
            logger.info("HystrixCommandFactory in guice=" + hystrixCommandFactory);
            notNull(hystrixCommandFactory, "HystrixCommandFactory '%s' not found", commandClass);

            // search for matching method
            Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
            Optional<Method> first = Arrays.stream(commandClass.getMethods())
                    .filter(method -> Arrays.equals(method.getParameterTypes(), parameterTypes))
                    .findFirst();
            Method method = first.orElseThrow(() -> new NullPointerException(
                    "No Method found in " + commandClass + " which accepts the parameters "
                            + Arrays.toString(parameterTypes) + ". Method: " + toSignatureString(invocation)));
            Object res = method.invoke(hystrixCommandFactory, invocation.getArguments());
            notNull(res, "HystrixCommandFactory '%s' returns null on invoking Method %s", hystrixCommandFactory, method);

            isAssignableFrom(InterceptableHystrixObservableCommand.class, res.getClass(),
                    "HystrixCommandFactory '%s' must return something assignable to '%s' on invoking Method %s",
                    hystrixCommandFactory, InterceptableHystrixObservableCommand.class, method);
            InterceptableHystrixObservableCommand command = (InterceptableHystrixObservableCommand) res;
            command.setObservable((Observable) invocation.proceed());
            return command.toObservable();
        } else if (InterceptableHystrixObservableCommand.class.isAssignableFrom(commandClass)) {
            logger.info("instanciating HystrixCommand...");
            // TODO (ak) impl
            throw new UnsupportedOperationException("not yet implemented");
        }
        throw new UnsupportedOperationException("not supported: " + commandClass);
    }

    private String toSignatureString(MethodInvocation invocation) {
        return invocation.getThis().getClass().getName() + "#" + invocation.getMethod().getName()
                + Arrays.toString(invocation.getMethod().getParameterTypes());
    }
}
