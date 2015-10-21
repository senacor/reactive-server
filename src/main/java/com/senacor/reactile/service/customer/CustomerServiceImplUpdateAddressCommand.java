package com.senacor.reactile.service.customer;

import com.google.inject.assistedinject.Assisted;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.senacor.reactile.hystrix.interception.InterceptableHystrixObservableCommand;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoService;

import javax.inject.Inject;

/**
 * Hystrix CustomerServiceImpl#UpdateAddress Command
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 17.04.15
 * Time: 09:40
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class CustomerServiceImplUpdateAddressCommand extends InterceptableHystrixObservableCommand<Customer> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImplUpdateAddressCommand.class);
    private final CustomerId customerId;
    private final Address address;
    private final Vertx vertx;
    private final MongoService mongoService;

    @Inject
    public CustomerServiceImplUpdateAddressCommand(Vertx vertx,
                                                      MongoService mongoService,
                                                      @Assisted CustomerId customerId,
                                                      @Assisted Address address) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Services"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("UpdateAddress"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)));
        this.vertx = vertx;
        this.mongoService = mongoService;
        this.customerId = customerId;
        this.address = address;
    }

}
