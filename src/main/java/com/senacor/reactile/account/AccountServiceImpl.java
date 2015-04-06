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

public class AccountServiceImpl implements AccountService {
    public static final String COLLECTION = "accounts";
    private final MongoService mongoService;

    @Inject
    public AccountServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getAccount(AccountId accountId, Handler<AsyncResult<Account>> resultHandler) {
        Observable<Account> result = mongoService.findOneObservable(COLLECTION, accountId.toJson(), null).map(Account::fromJson);
        Rx.bridgeHandler(result, resultHandler);
    }

    @Override
    public void getAccountsForCustomer(CustomerId customerId, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        mongoService.find(COLLECTION, query, resultHandler);
    }

    @Override
    public void createAccount(Account account, Handler<AsyncResult<String>> resultHandler) {
        JsonObject doc = account.toJson().put("_id", account.getId().toValue());
        mongoService.insert(COLLECTION, doc, resultHandler);
    }

}
