package com.senacor.reactile.service.account;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.abstractservice.JsonizableList;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import java.util.List;

public interface AccountService {

    @Action(returnType = Account.class)
    public Observable<Account> getAccount(AccountId accountId);

    @Action(returnType = JsonizableList.class)
    public Observable<JsonizableList<JsonObject>> getAccountsForCustomer(CustomerId customerId);

    @Action(returnType = Account.class)
    public Observable<Account> createAccount(Account account);
}
