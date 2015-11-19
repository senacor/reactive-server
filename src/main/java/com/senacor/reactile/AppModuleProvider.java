package com.senacor.reactile;

import com.deenterprised.vertx.spi.BootstrapModuleProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.senacor.reactile.abstractservice.ObserverProxy;
import com.senacor.reactile.gateway.commands.*;
import com.senacor.reactile.guice.Blocking;
import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.hystrix.interception.HystrixCmd;
import com.senacor.reactile.hystrix.interception.HystrixCommandInterceptor;
import com.senacor.reactile.hystrix.metrics.eventstream.MetricsBridge;
import com.senacor.reactile.service.account.AccountService;
import com.senacor.reactile.service.account.AccountServiceImpl;
import com.senacor.reactile.service.account.TransactionService;
import com.senacor.reactile.service.account.TransactionServiceImpl;
import com.senacor.reactile.service.appointment.AppointmentDatabase;
import com.senacor.reactile.service.branch.BranchDatabase;
import com.senacor.reactile.service.branch.BranchService;
import com.senacor.reactile.service.branch.BranchServiceImpl;
import com.senacor.reactile.service.creditcard.CreditCardService;
import com.senacor.reactile.service.creditcard.CreditCardServiceImpl;
import com.senacor.reactile.service.customer.CustomerService;
import com.senacor.reactile.service.customer.CustomerServiceImpl;
import com.senacor.reactile.service.newsticker.NewsService;
import com.senacor.reactile.service.newsticker.NewsServiceImpl;
import com.senacor.reactile.service.user.UserService;
import com.senacor.reactile.service.user.UserServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.RxHelper;
import rx.Scheduler;

import java.lang.reflect.Proxy;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

public class AppModuleProvider implements BootstrapModuleProvider {
    @Override
    public Module get() {
        return new AppModule();
    }

    @Override
    public int priority() {
        return 0;
    }

    public static class AppModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(UserService.class).annotatedWith(Impl.class).to(UserServiceImpl.class);
            bind(AccountService.class).annotatedWith(Impl.class).to(AccountServiceImpl.class);
            bind(CreditCardService.class).annotatedWith(Impl.class).to(CreditCardServiceImpl.class);
            bind(TransactionService.class).annotatedWith(Impl.class).to(TransactionServiceImpl.class);
            bind(CustomerService.class).annotatedWith(Impl.class).to(CustomerServiceImpl.class);
            bind(BranchService.class).annotatedWith(Impl.class).to(BranchServiceImpl.class);
            bind(NewsService.class).annotatedWith(Impl.class).to(NewsServiceImpl.class);
            bind(AppointmentDatabase.class).in(Scopes.SINGLETON);
            bind(BranchDatabase.class).in(Scopes.SINGLETON);
            bind(MetricsBridge.class);

            // Install  HystrixComand Factories
            install(new FactoryModuleBuilder()
                    .implement(CustomerUpdateAddressCommand.class, CustomerUpdateAddressCommand.class)
                    .build(CustomerUpdateAddressCommandFactory.class));
            install(new FactoryModuleBuilder()
                    .implement(StartCommand.class, StartCommand.class)
                    .build(StartCommandFactory.class));
            install(new FactoryModuleBuilder()
                    .implement(UserReadCommand.class, UserReadCommand.class)
                    .build(UserReadCommandFactory.class));
            install(new FactoryModuleBuilder()
                    .implement(UserFindCommand.class, UserFindCommand.class)
                    .build(UserFindCommandFactory.class));

            HystrixCommandInterceptor hystrixCommandInterceptor = new HystrixCommandInterceptor();
            requestInjection(hystrixCommandInterceptor);
            bindInterceptor(any(), annotatedWith(HystrixCmd.class), hystrixCommandInterceptor);
        }

        @Provides
        Scheduler provideRxScheduler(Vertx vertx) {
            return RxHelper.scheduler(vertx);
        }

        @Provides
        @Blocking
        Scheduler provideBlockingRxScheduler(Vertx vertx) {
            return RxHelper.blockingScheduler(vertx);
        }

        @Provides
        MongoService provideMongoService(Vertx vertx) {
            return MongoService.createEventBusProxy(vertx, "vertx.mongo");
        }

        @Provides
        io.vertx.rxjava.ext.mongo.MongoService provideMongoService(MongoService mongoService) {
            return new io.vertx.rxjava.ext.mongo.MongoService(mongoService);
        }

        @Provides
        CustomerService provideCustomerService(Vertx vertx) {
            return (CustomerService) Proxy.newProxyInstance(
                    CustomerService.class.getClassLoader(),
                    new Class[]{CustomerService.class},
                    new ObserverProxy(vertx, "CustomerVerticle"));
        }

        @Provides
        BranchService provideBranchService(Vertx vertx) {
            return (BranchService) Proxy.newProxyInstance(
                    BranchService.class.getClassLoader(),
                    new Class[]{BranchService.class},
                    new ObserverProxy(vertx, "BranchVerticle"));
        }

        @Provides
        CreditCardService provideCreditCardService(Vertx vertx) {
            return (CreditCardService) Proxy.newProxyInstance(
                    CreditCardService.class.getClassLoader(),
                    new Class[]{CreditCardService.class},
                    new ObserverProxy(vertx, "CreditCardVerticle"));
        }

        @Provides
        AccountService provideAccountService(Vertx vertx) {
            return (AccountService) Proxy.newProxyInstance(
                    AccountService.class.getClassLoader(),
                    new Class[]{AccountService.class},
                    new ObserverProxy(vertx, "AccountVerticle"));
        }

        @Provides
        NewsService provideNewsService(Vertx vertx) {
            return (NewsService) Proxy.newProxyInstance(
                    NewsService.class.getClassLoader(),
                    new Class[]{NewsService.class},
                    new ObserverProxy(vertx, "NewsVerticle"));
        }

        @Provides
        TransactionService provideTransactionService(Vertx vertx) {
            return (TransactionService) Proxy.newProxyInstance(
                    TransactionService.class.getClassLoader(),
                    new Class[]{TransactionService.class},
                    new ObserverProxy(vertx, "TransactionVerticle"));
        }

        @Provides
        UserService provideUserService(Vertx vertx) {
            return (UserService) Proxy.newProxyInstance(
                    UserService.class.getClassLoader(),
                    new Class[]{UserService.class},
                    new ObserverProxy(vertx, "UserVerticle"));
        }


    }
}
