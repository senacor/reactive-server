package com.senacor.reactile.service.account;

import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.hystrix.interception.HystrixCmd;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    public static final String COLLECTION = "accounts";
    private final MongoService mongoService;

    @Inject
    public AccountServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getAccount(AccountId accountId, Handler<AsyncResult<Account>> resultHandler) {
        Observable<Account> accountObservable = mongoService.findOneObservable(COLLECTION, accountId.toJson(), null).map(Account::fromJson);
        Rx.bridgeHandler(accountObservable, resultHandler);
    }

    @Override
    public void getAccountsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        Rx.bridgeHandler(getAccountsForCustomer(customerId), resultHandler);
    }

    private Observable<List<JsonObject>> getAccountsForCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        return mongoService.findObservable(COLLECTION, query);
    }

    @Override
    public void createAccount(Account account, Handler<AsyncResult<Account>> resultHandler) {
        JsonObject doc = account.toJson().put("_id", account.getId().toValue());
        Observable<Account> accountObservable = mongoService.insertObservable(COLLECTION, doc)
                .flatMap(id -> Observable.just(account));
        Rx.bridgeHandler(accountObservable, resultHandler);
    }

}
