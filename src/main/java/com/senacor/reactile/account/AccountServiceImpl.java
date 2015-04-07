package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class AccountServiceImpl implements AccountService {
    public static final String COLLECTION = "accounts";
    private final MongoService mongoService;

    @Inject
    public AccountServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getAccount(AccountId accountId, Handler<AsyncResult<Account>> resultHandler) {
        mongoService.findOneObservable(COLLECTION, accountId.toJson(), null)
                .map(Account::fromJson)
                .subscribe(Rx.toSubscriber(resultHandler));
    }

    @Override
    public void getAccountsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        mongoService.findObservable(COLLECTION, query)
                .subscribe(Rx.toSubscriber(resultHandler));
    }

    @Override
    public void createAccount(Account account, Handler<AsyncResult<Account>> resultHandler) {
        JsonObject doc = account.toJson().put("_id", account.getId().toValue());
        mongoService.insertObservable(COLLECTION, doc)
                .flatMap(id -> Observable.just(account))
                .subscribe(Rx.toSubscriber(resultHandler))
        ;
    }

}
