package com.senacor.reactile.account;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.MongoBootstrap;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.senacor.reactile.account.CreditCardFixtures.newCreditCardWithCustomer;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by rwinzing on 03.03.15.
 */
public class CreditCardServiceVerticleTest {
    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.EmbeddedMongo, Services.CreditCardService);
    private final MongoInitializer initializer = new MongoInitializer(vertxRule.vertx(), "creditcards");

    static {
        vertxRule.deployVerticle(MongoBootstrap.class);
    }


    @Test
    public void thatMultipleCreditCardsAreReturned() throws InterruptedException, ExecutionException, TimeoutException {
        CreditCard creditCard1 = newCreditCardWithCustomer("cust-0816");
        CreditCard creditCard2 = newCreditCardWithCustomer("cust-0816");
        CreditCard creditCard3 = newCreditCardWithCustomer("cust-0816");
        initializer.writeBlocking(creditCard1, creditCard2, creditCard3);
        CustomerId customerId = new CustomerId("cust-0816");
        Message<List<CreditCard>> written = vertxRule.sendBlocking(CreditCardServiceVerticle.ADDRESS, customerId, "getCreditCardsForCustomer");
        assertThat(written.body(), hasSize(3));
    }

    @Test
    public void thatSpecificCreditCardIsReturned() throws InterruptedException, ExecutionException, TimeoutException {
        CreditCard creditCard = CreditCardFixtures.defaultCreditCard().withId("ASDFGH").withBalance(BigDecimal.TEN).build();
        initializer.writeBlocking(creditCard);
        CreditCard card = vertxRule.<CreditCard>sendBlocking(CreditCardServiceVerticle.ADDRESS, creditCard.getId(), "get").body();
        assertThat(card.getBalance(), is(equalTo(BigDecimal.TEN)));
    }
}
