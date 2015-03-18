package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.ObservableMongoService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    public static final String COLLECTION = "accounts";
    private final ObservableMongoService mongoService;

    @Inject
    public AccountServiceImpl(ObservableMongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getAccount(AccountId accountId, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoService.findOne(COLLECTION, accountId.toJson(), null, resultHandler);

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
