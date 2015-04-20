package com.senacor.reactile.hystrix.interception;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;

/**
 * HystrixObservableCommand which is used by HystrixCommandInterceptor to set the target Observable
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 17.04.15
 * Time: 14:31
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public abstract class InterceptableHystrixObservableCommand<T> extends HystrixObservableCommand<T> {

    protected Observable<T> observable;

    protected InterceptableHystrixObservableCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    protected InterceptableHystrixObservableCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected Observable<T> construct() {
        return observable;
    }

    public InterceptableHystrixObservableCommand<T> withObservable(Observable<T> observable) {
        this.observable = observable;
        return this;
    }
}
