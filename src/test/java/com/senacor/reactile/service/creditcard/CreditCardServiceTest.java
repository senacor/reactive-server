package com.senacor.reactile.service.creditcard;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.service.creditcard.CreditCard;
import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static com.senacor.reactile.service.creditcard.CreditCardFixtures.newCreditCard;
import static com.senacor.reactile.service.creditcard.CreditCardFixtures.randomCreditCard;
import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperty;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasValue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class CreditCardServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.CreditCardService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private CreditCardService service;


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
        List<CreditCard> creditCards = service.getCreditCardsForCustomer(new CustomerId("cust-001")).toBlocking().first().getCreditCardList();
        assertThat(creditCards, hasSize(2));
        assertThat(creditCards, hasItems(hasId("cc-002"), hasId("cc-003")));
    }

    @Test
    public void thatCreditCardCanBeCreated() {
        CreditCard creditCard = service.createCreditCard(randomCreditCard("cc-004", "cust-004")).toBlocking().first();
        assertThat(creditCard.toJson(), hasProperty("id"));
        assertThat(creditCard.toJson(), hasValue("id", "cc-004"));
        assertThat(creditCard.toJson(), hasValue("customerId", "cust-004"));
    }


}