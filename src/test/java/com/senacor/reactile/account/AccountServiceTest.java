package com.senacor.reactile.account;

import com.senacor.reactile.TestServices;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.MongoInitializer;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.senacor.reactile.domain.IdentityMatcher.hasId;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class AccountServiceTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(TestServices.AccountService);
    private final AccountService service = new AccountServiceImpl(vertxRule.vertx());

    private final MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "accounts");

    @Test
    public void thatSingleAccountIsReturned_forAccountId() {
        mongoInitializer.writeBlocking(AccountFixtures.randomAccount("acc-32423"));
        Account account = service.getAccount(new AccountId("acc-32423")).toBlocking().first();
        assertThat(account, hasId("acc-32423"));
    }

    @Test
    public void thatMultipleAccountsAreReturned_forCustomer() {
        mongoInitializer.writeBlocking(AccountFixtures.randomAccount("acc-001", "cust-001"));
        mongoInitializer.writeBlocking(AccountFixtures.randomAccount("acc-002", "cust-001"));
        List<Account> accounts = service.getAccountsForCustomer(new CustomerId("cust-001")).toBlocking().first();
        assertThat(accounts, hasSize(2));
        assertThat(accounts, hasItems(hasId("acc-001"), hasId("acc-002")));
    }


}