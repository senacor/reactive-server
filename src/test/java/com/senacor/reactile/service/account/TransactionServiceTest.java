package com.senacor.reactile.service.account;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static com.senacor.reactile.domain.JsonizableMatchers.hasProperty;
import static com.senacor.reactile.domain.JsonizableMatchers.hasValue;
import static com.senacor.reactile.service.account.TransactionFixtures.newAccTransaction;
import static com.senacor.reactile.service.account.TransactionFixtures.newCCTransaction;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class TransactionServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.TransactionService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Rule
    public final MongoInitializer initializer = new MongoInitializer(vertxRule.vertx(), TransactionServiceImpl.COLLECTION);

    @Inject
    private com.senacor.reactile.rxjava.service.account.TransactionService service;

    @Before
    public void init() {
        initializer.writeBlocking(
                newCCTransaction("cust-5678", "cc-1234567890"),
                newCCTransaction("cust-5678", "cc-123"),
                newAccTransaction("cust-5678", "acc-1234567890"),
                newAccTransaction("cust-1111", "acc-1234567890"));

    }

    @Test
    public void thatTransactionsAreReturned_forAccountId() {
        TransactionList transactions = service.getTransactionsForAccountObservable(new AccountId("acc-1234567890")).toBlocking().first();
        assertThat(transactions.getTransactionList(), hasSize(2));
    }

    @Test
    public void thatTransactionsAreReturned_forCrediCardId() {
        TransactionList transactions = service.getTransactionsForCreditCardObservable(new CreditCardId("cc-123")).toBlocking().first();
        assertThat(transactions.getTransactionList(), hasSize(1));
    }

    @Test
    public void thatTransactionsAreReturned_forCustomerId() {
        TransactionList transactions = service.getTransactionsForCustomerObservable(new CustomerId("cust-5678")).toBlocking().first();
        assertThat(transactions.getTransactionList(), hasSize(3));
    }

    @Test
    public void thatTransactionsCanBeCreated() {
        Transaction accTransaction = service.createTransactionObservable(newAccTransaction("cust-456", "acc-555")).toBlocking().first();
        assertThat(accTransaction, hasProperty("id"));
        assertThat(accTransaction, hasValue("customerId", "cust-456"));
        assertThat(accTransaction, hasValue("accountId", "acc-555"));
        Transaction ccTransaction = service.createTransactionObservable(newCCTransaction("cust-456", "cc-555")).toBlocking().first();
        assertThat(ccTransaction, hasProperty("id"));
        assertThat(ccTransaction, hasValue("customerId", "cust-456"));
        assertThat(ccTransaction, hasValue("creditCardId", "cc-555"));

    }

}