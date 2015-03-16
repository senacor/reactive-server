package com.senacor.reactile.account;

import com.senacor.reactile.TestServices;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.MongoInitializer;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static com.senacor.reactile.account.TransactionFixtures.newAccTransaction;
import static com.senacor.reactile.account.TransactionFixtures.newCCTransaction;
import static com.senacor.reactile.domain.JsonizableMatchers.hasProperty;
import static com.senacor.reactile.domain.JsonizableMatchers.hasValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class TransactionServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(TestServices.TransactionService);
    private final TransactionService service = new TransactionServiceImpl(vertxRule.vertx());

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
    public void thatTransactionsAreReturned_forAccountId() {
        List<Transaction> transactions = service.getTransactionsForAccount(new AccountId("acc-1234567890")).toBlocking().first();
        assertThat(transactions, hasSize(2));
    }

    @Test
    public void thatTransactionsAreReturned_forCrediCardId() {
        List<Transaction> transactions = service.getTransactionsForCreditCard(new CreditCardId("cc-123")).toBlocking().first();
        assertThat(transactions, hasSize(1));
    }

    @Test
    public void thatTransactionsAreReturned_forCustomerId() {
        List<Transaction> transactions = service.getTransactionsForCustomer(new CustomerId("cust-5678")).toBlocking().first();
        assertThat(transactions, hasSize(3));
    }

    @Test
    public void thatTransactionsCanBeCreated() {
        Transaction accTransaction = service.createTransaction(newAccTransaction("cust-456", "acc-555")).toBlocking().first();
        assertThat(accTransaction, hasProperty("id"));
        assertThat(accTransaction, hasValue("customerId", "cust-456"));
        assertThat(accTransaction, hasValue("accountId", "acc-555"));
        Transaction ccTransaction = service.createTransaction(newCCTransaction("cust-456", "cc-555")).toBlocking().first();
        assertThat(ccTransaction, hasProperty("id"));
        assertThat(ccTransaction, hasValue("customerId", "cust-456"));
        assertThat(ccTransaction, hasValue("creditCardId", "cc-555"));

    }

}