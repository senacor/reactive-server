package com.senacor.reactile.service.account;

import com.senacor.reactile.json.JsonizableList;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;

public class AccountServiceImpl implements AccountService {
    public static final String COLLECTION = "accounts";
    private final MongoService mongoService;

    @Inject
    public AccountServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }


    @Override
    public Observable<Account> getAccount(AccountId accountId)  {
        return mongoService.findOneObservable(COLLECTION, accountId.toJson(), null).map(Account::fromJson);
    }

    @Override
    public Observable<JsonizableList<JsonObject>> getAccountsForCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        return mongoService.findObservable(COLLECTION, query)
                .map(list -> new JsonizableList<JsonObject>(list));
    }

    @Override
    public Observable<Account> createAccount(Account account) {
        JsonObject doc = account.toJson().put("_id", account.getId().toValue());
        return mongoService.insertObservable(COLLECTION, doc)
                .flatMap(id -> Observable.just(account));
    }

}
