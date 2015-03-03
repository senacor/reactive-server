package com.senacor.reactile.account;

import com.senacor.reactile.EventBusRule;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.ApplicationStartup;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.customer.CustomerServiceVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by rwinzing on 03.03.15.
 */
public class AccountServiceVerticleTest {
    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class);
    {
        vertxRule.deployVerticle(Services.AccountService);
    }

    @Rule
    public final EventBusRule eventBusRule = new EventBusRule(vertxRule.vertx());

    @Test
    public void thatMultipleAccountsAreReturned() throws InterruptedException, ExecutionException, TimeoutException {
        CustomerId customerId = new CustomerId("08-cust-15");

        Message<List<Account>> accsMsg = eventBusRule.sendObservable(AccountServiceVerticle.ADDRESS, customerId, "getAccountsForCustomer");
        for(Account acc:accsMsg.body()) {
            System.out.println("ACCOUNT: " + acc);
        }

        assertThat(accsMsg.body().size(), is(equalTo(2)));
    }

    @Test
    public void thatSpecificAccountIsReturned() throws InterruptedException, ExecutionException, TimeoutException {
        AccountId accountId = new AccountId("08-cust-15-ac-2");

        Message<Account> accMsg = eventBusRule.sendObservable(AccountServiceVerticle.ADDRESS, accountId, "getAccount");
        System.out.println("ACCOUNT: " + accMsg.body());

        assertThat(accMsg.body().getBalance(), is(equalTo(new BigDecimal("20773"))));
    }
}
