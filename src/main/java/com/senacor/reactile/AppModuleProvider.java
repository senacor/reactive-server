package com.senacor.reactile;

import com.deenterprised.vertx.spi.BootstrapModuleProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.senacor.reactile.account.AccountService;
import com.senacor.reactile.account.AccountServiceImpl;
import com.senacor.reactile.account.TransactionService;
import com.senacor.reactile.account.TransactionServiceImpl;
import com.senacor.reactile.creditcard.CreditCardService;
import com.senacor.reactile.creditcard.CreditCardServiceImpl;
import com.senacor.reactile.customer.CustomerService;
import com.senacor.reactile.customer.CustomerServiceImpl;
import com.senacor.reactile.gateway.commands.CustomerUpdateAddressCommand;
import com.senacor.reactile.gateway.commands.CustomerUpdateAddressCommandFactory;
import com.senacor.reactile.gateway.commands.StartCommand;
import com.senacor.reactile.gateway.commands.StartCommandFactory;
import com.senacor.reactile.guice.Blocking;
import com.senacor.reactile.guice.Impl;
import com.senacor.reactile.user.UserConnector;
import com.senacor.reactile.user.UserService;
import com.senacor.reactile.user.UserServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.RxHelper;
import io.vertx.serviceproxy.ProxyHelper;
import rx.Scheduler;

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
            bind(UserService.class).to(UserServiceImpl.class);
            bind(AccountService.class).annotatedWith(Impl.class).to(AccountServiceImpl.class);
            bind(CreditCardService.class).annotatedWith(Impl.class).to(CreditCardServiceImpl.class);
            bind(TransactionService.class).to(TransactionServiceImpl.class);
            bind(CustomerService.class).annotatedWith(Impl.class).to(CustomerServiceImpl.class);
            bind(UserConnector.class);

            // Install  HystrixComand Factories
            install(new FactoryModuleBuilder()
                    .implement(CustomerUpdateAddressCommand.class, CustomerUpdateAddressCommand.class)
                    .build(CustomerUpdateAddressCommandFactory.class));
            install(new FactoryModuleBuilder()
                    .implement(StartCommand.class, StartCommand.class)
                    .build(StartCommandFactory.class));
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
        com.senacor.reactile.rxjava.customer.CustomerService provideCustomerService(Vertx vertx) {
            CustomerService proxy = ProxyHelper.createProxy(CustomerService.class, vertx, CustomerService.ADDRESS);
            return new com.senacor.reactile.rxjava.customer.CustomerService(proxy);
        }

        @Provides
        CreditCardService provideCreditCardService(Vertx vertx) {
            return ProxyHelper.createProxy(CreditCardService.class, vertx, CreditCardService.ADDRESS);
        }

        @Provides
        com.senacor.reactile.rxjava.account.AccountService provideAccountService(Vertx vertx) {
            AccountService proxy = ProxyHelper.createProxy(AccountService.class, vertx, AccountService.ADDRESS);
            return new com.senacor.reactile.rxjava.account.AccountService(proxy);
        }
    }
}
