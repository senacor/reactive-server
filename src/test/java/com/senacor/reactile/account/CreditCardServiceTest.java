package com.senacor.reactile.account;

import com.senacor.reactile.TestServices;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.MongoInitializer;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static com.senacor.reactile.account.CreditCardFixtures.newCreditCard;
import static com.senacor.reactile.account.CreditCardFixtures.randomCreditCard;
import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperty;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasValue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class CreditCardServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(TestServices.CreditCardService);
    private final CreditCardService service = new CreditCardServiceImpl(vertxRule.vertx());

    private final MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "creditcards");

    @Test
    public void thatSingleCreditCardIsReturned_forCreditCardId() {
        mongoInitializer.writeBlocking(newCreditCard("cc-001"));
        CreditCard creditCard = service.getCreditCard(new CreditCardId("cc-001")).toBlocking().first();
        assertThat(creditCard, hasId("cc-001"));
        assertThat(creditCard.toJson(), allOf(hasValue("id", "cc-001"), hasProperty("customerId")));
    }

    @Test
    public void thatMultipleCreditCardsAreReturned_forCustomer() {
        mongoInitializer.writeBlocking(randomCreditCard("cc-002", "cust-001"), randomCreditCard("cc-003", "cust-001"));
        List<CreditCard> creditCards = service.getCreditCardsForCustomer(new CustomerId("cust-001")).toBlocking().first();
        assertThat(creditCards, hasSize(2));
        assertThat(creditCards, hasItems(hasId("cc-002"), hasId("cc-003")));
    }

    @Test
    public void thatCreditCardCanBeCreated() {
        CreditCard creditCard = service.createCreditCard(randomCreditCard("cc-003", "cust-003")).toBlocking().first();
        assertThat(creditCard.toJson(), hasProperty("id"));
        assertThat(creditCard.toJson(), hasValue("id", "cc-003"));
        assertThat(creditCard.toJson(), hasValue("customerId", "cust-003"));
    }


}