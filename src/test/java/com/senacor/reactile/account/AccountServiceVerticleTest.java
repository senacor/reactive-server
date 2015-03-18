package com.senacor.reactile.account;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.domain.Amount;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.senacor.reactile.account.AccountFixtures.newAccount1;
import static com.senacor.reactile.account.AccountFixtures.newAccount2;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AccountServiceVerticleTest {
    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.AccountService);

    private static final MongoInitializer initializer = new MongoInitializer(vertxRule.vertx(), "accounts");

    @BeforeClass
    public static void init() {
     initializer.writeBlocking(newAccount1());
     initializer.writeBlocking(newAccount2());
    }

    @Test
    public void thatMultipleAccountsAreReturned() throws InterruptedException, ExecutionException, TimeoutException {
        CustomerId customerId = newAccount1().getCustomerId();
        Message<List<Account>> accounts = vertxRule.sendBlocking(AccountServiceVerticle.ADDRESS, customerId, "getAccountsForCustomer");
        assertThat(accounts.body(), hasSize(2));
    }

    @Test
    public void thatSpecificAccountIsReturned() throws InterruptedException, ExecutionException, TimeoutException {
        AccountId accountId = newAccount2().getId();
        Account account = vertxRule.<Account>sendBlocking(AccountServiceVerticle.ADDRESS, accountId, "get").body();
        assertThat(account.getBalance(), is(equalTo(new Amount(new BigDecimal(20773)))));
    }
}
