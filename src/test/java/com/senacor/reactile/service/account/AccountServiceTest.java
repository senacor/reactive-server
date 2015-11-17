package com.senacor.reactile.service.account;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.service.customer.CustomerId;
import io.vertx.core.json.JsonObject;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperty;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasValue;
import static com.senacor.reactile.service.account.AccountFixtures.randomAccount;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class AccountServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AccountService);
    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private AccountService service;

    private final MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), AccountServiceImpl.COLLECTION);

    @Test
    public void thatSingleAccountIsReturned_forAccountId() {
        mongoInitializer.writeBlocking(randomAccount("acc-32423"));
        Account account = service.getAccount(new AccountId("acc-32423")).toBlocking().first();
        assertThat(account, hasId("acc-32423"));
    }

    @Test
    public void thatMultipleAccountsAreReturned_forCustomer() {
        mongoInitializer.writeBlocking(randomAccount("acc-001", "cust-001"));
        mongoInitializer.writeBlocking(randomAccount("acc-002", "cust-001"));
        List<JsonObject> accounts = service
                .getAccountsForCustomer(new CustomerId("cust-001"))
                .map(jsonizableList ->jsonizableList.toList())
                .toBlocking().first();
        assertThat(accounts, hasSize(2));
        assertThat(accounts, hasItems(hasValue("id", "acc-001"), hasValue("id", "acc-002")));
    }

    @Test
    public void thatAccountCanBeCreated() {
        Account account = service.createAccount(randomAccount("acc-003", "cust-003")).toBlocking().first();
        assertThat(account.toJson(), hasProperty("id"));
        assertThat(account.toJson(), hasValue("id", "acc-003"));
        assertThat(account.toJson(), hasValue("customerId", "cust-003"));
    }


}