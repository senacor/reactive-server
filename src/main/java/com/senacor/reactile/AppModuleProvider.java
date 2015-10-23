package com.senacor.reactile;

import com.deenterprised.vertx.spi.BootstrapModuleProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
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
import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.appointment.AppointmentServiceImpl;
import com.senacor.reactile.service.branch.BranchDatabase;
import com.senacor.reactile.service.branch.BranchService;
import com.senacor.reactile.service.branch.BranchServiceImpl;
import com.senacor.reactile.service.creditcard.CreditCardService;
import com.senacor.reactile.service.creditcard.CreditCardServiceImpl;
import com.senacor.reactile.service.customer.CustomerService;
import com.senacor.reactile.service.customer.CustomerServiceImpl;
import com.senacor.reactile.service.user.UserService;
import com.senacor.reactile.service.user.UserServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.RxHelper;
import io.vertx.serviceproxy.ProxyHelper;
import rx.Scheduler;

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
            bind(BranchService.class).annotatedWith(Impl.class).to(BranchServiceImpl.class);
            bind(AccountService.class).annotatedWith(Impl.class).to(AccountServiceImpl.class);
            bind(CreditCardService.class).annotatedWith(Impl.class).to(CreditCardServiceImpl.class);
            bind(TransactionService.class).annotatedWith(Impl.class).to(TransactionServiceImpl.class);
            bind(CustomerService.class).annotatedWith(Impl.class).to(CustomerServiceImpl.class);
            bind(AppointmentService.class).annotatedWith(Impl.class).to(AppointmentServiceImpl.class);
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
                    .implement(AppointmentsSummaryCommand.class, AppointmentsSummaryCommand.class)
                    .build(AppointmentsSummaryCommandFactory.class));
            install(new FactoryModuleBuilder()
                    .implement(GetAppointmentCommand.class, GetAppointmentCommand.class)
                    .build(GetAppointmentCommandFactory.class));
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
        com.senacor.reactile.rxjava.service.customer.CustomerService provideCustomerService(Vertx vertx) {
            CustomerService proxy = ProxyHelper.createProxy(CustomerService.class, vertx, CustomerService.ADDRESS);
            return new com.senacor.reactile.rxjava.service.customer.CustomerService(proxy);
        }

        @Provides
        com.senacor.reactile.rxjava.service.branch.BranchService provideBranchService(Vertx vertx) {
            BranchService proxy = ProxyHelper.createProxy(BranchService.class, vertx, BranchService.ADDRESS);
            return new com.senacor.reactile.rxjava.service.branch.BranchService(proxy);
        }

        @Provides
        com.senacor.reactile.rxjava.service.creditcard.CreditCardService provideCreditCardService(Vertx vertx) {
            CreditCardService proxy = ProxyHelper.createProxy(CreditCardService.class, vertx, CreditCardService.ADDRESS);
            return new com.senacor.reactile.rxjava.service.creditcard.CreditCardService(proxy);
        }

        @Provides
        com.senacor.reactile.rxjava.service.account.AccountService provideAccountService(Vertx vertx) {
            AccountService proxy = ProxyHelper.createProxy(AccountService.class, vertx, AccountService.ADDRESS);
            return new com.senacor.reactile.rxjava.service.account.AccountService(proxy);
        }

        @Provides
        com.senacor.reactile.rxjava.service.account.TransactionService provideTransactionService(Vertx vertx) {
            TransactionService proxy = ProxyHelper.createProxy(TransactionService.class, vertx, TransactionService.ADDRESS);
            return new com.senacor.reactile.rxjava.service.account.TransactionService(proxy);
        }

        @Provides
        com.senacor.reactile.rxjava.service.user.UserService provideUserService(Vertx vertx) {
            UserService proxy = ProxyHelper.createProxy(UserService.class, vertx, UserService.ADDRESS);
            return new com.senacor.reactile.rxjava.service.user.UserService(proxy);
        }

        @Provides
        com.senacor.reactile.rxjava.service.appointment.AppointmentService provideAppointmentService(Vertx vertx) {
            AppointmentService proxy = ProxyHelper.createProxy(AppointmentService.class, vertx, AppointmentService.ADDRESS);
            return new com.senacor.reactile.rxjava.service.appointment.AppointmentService(proxy);
        }

    }
}
