package com.senacor.reactile;

import com.deenterprised.vertx.spi.BootstrapModuleProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.senacor.reactile.account.AccountService;
import com.senacor.reactile.account.AccountServiceImpl;
import com.senacor.reactile.account.CreditCardService;
import com.senacor.reactile.account.CreditCardServiceImpl;
import com.senacor.reactile.account.TransactionService;
import com.senacor.reactile.account.TransactionServiceImpl;
import com.senacor.reactile.customer.CustomerService;
import com.senacor.reactile.customer.CustomerServiceImpl;
import com.senacor.reactile.mongo.ObservableMongoService;
import com.senacor.reactile.user.UserConnector;
import com.senacor.reactile.user.UserService;
import com.senacor.reactile.user.UserServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoService;

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
            bind(AccountService.class).to(AccountServiceImpl.class);
            bind(CustomerService.class).to(CustomerServiceImpl.class);
            bind(CreditCardService.class).to(CreditCardServiceImpl.class);
            bind(TransactionService.class).to(TransactionServiceImpl.class);
            bind(UserConnector.class);
        }

        @Provides
        MongoService provideMongoService(Vertx vertx) {
            return MongoService.createEventBusProxy(vertx, "vertx.mongo");
        }

        @Provides
        ObservableMongoService provideMongoService(MongoService mongoService) {
            return ObservableMongoService.from(mongoService);
        }
    }
}
