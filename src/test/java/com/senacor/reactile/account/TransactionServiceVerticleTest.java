package com.senacor.reactile.account;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.MongoBootstrap;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static com.senacor.reactile.account.TransactionFixtures.newAccTransaction;
import static com.senacor.reactile.account.TransactionFixtures.newCCTransaction;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class TransactionServiceVerticleTest {

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.EmbeddedMongo, Services.TransactionService);

    static {
        vertxRule.deployVerticle(MongoBootstrap.class);
    }

    private static final MongoInitializer initializer = new MongoInitializer(vertxRule.vertx(), "transactions");

    @BeforeClass
    public static void init() {
        initializer.writeBlocking(
                newCCTransaction("cust-5678", "cc-1234567890"),
                newCCTransaction("cust-5678", "cc-123"),
                newAccTransaction("cust-5678", "acc-1234567890"),
                newAccTransaction("cust-1111", "acc-1234567890"));
    }

    @Test
    public void thatTransactionsCanBeRetrieved_byCustomerId() throws Exception {
        Message<List<Transaction>> transactions = vertxRule.sendBlocking(TransactionServiceVerticle.ADDRESS, new CustomerId("cust-5678"), "getTransactionsForCustomer");
        assertThat(transactions.body(), hasSize(3));
    }

    @Test
    public void thatTransactionsCanBeRetrieved_byAccountId() throws Exception {
        Message<List<Transaction>> transactions = vertxRule.sendBlocking(TransactionServiceVerticle.ADDRESS, new AccountId("acc-1234567890"), "getTransactionsForAccount");
        assertThat(transactions.body(), hasSize(2));
    }

    @Test
    public void thatTransactionsCanBeRetrieved_byCreditCardId() throws Exception {
        Message<List<Transaction>> transactions = vertxRule.sendBlocking(TransactionServiceVerticle.ADDRESS, new CreditCardId("cc-123"), "getTransactionsForCreditCard");
        assertThat(transactions.body(), hasSize(1));
    }

}
