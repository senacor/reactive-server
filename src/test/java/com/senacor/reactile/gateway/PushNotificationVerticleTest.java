package com.senacor.reactile.gateway;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.common.base.Throwables;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerAddressChangedEvt;
import com.senacor.reactile.customer.CustomerFixtures;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.newsticker.News;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import rx.observers.TestSubscriber;

public class PushNotificationVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationVerticleTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.CustomerService)
            .deployVerticle(PushNotificationVerticle.class)
            .deployVerticle(Services.NewsService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.customer.CustomerService service;

    @Test
    public void testCustomerUpdateEvent() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();

        final LinkedBlockingQueue<CustomerAddressChangedEvt> queue = new LinkedBlockingQueue<>();

        // listen for events
        String eventAddress = PushNotificationVerticle.PUBLISH_ADDRESS_CUSTOMER_ADDRESS_UPDATE + customer.getId().getId();
        logger.info("listening on address '" + eventAddress + "'");
        vertxRule.eventBus().consumer(eventAddress)
                .toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(CustomerAddressChangedEvt::fromJson)
                .subscribe(queue::add,
                        throwable -> fail(throwable.getMessage() + Throwables.getStackTraceAsString(throwable)));

        // create customer and update Address
        service.createCustomerObservable(customer)
                .map(customerCreated -> Address.anAddress()
                        .withAddress(customerCreated.getAddresses().get(0))
                        .withZipCode("00815")
                        .withCity("NewCity")
                        .build())
                .flatMap(newAddress -> service.updateAddressObservable(customer.getId(), newAddress))
                .subscribe(customerWithUpdatedAddress -> logger.info("updateAddress: " + customerWithUpdatedAddress));

        CustomerAddressChangedEvt event = queue.poll(5L, TimeUnit.SECONDS);
        logger.info("Received Event: " + event);
        assertNotNull("CustomerAddressChangedEvt not received", event);
        assertEquals("event.newAddress.city", "NewCity", event.getNewAddress().getCity());
    }

    @Test
    public void testNewsUpdateEvent() throws Exception {
        TestSubscriber<News> ts = new TestSubscriber<News>();

        // listen for events
        String eventAddress = PushNotificationVerticle.PUBLISH_NEWS_UPDATE;
        logger.info("listening on address '" + eventAddress + "'");
        vertxRule.eventBus().consumer(eventAddress)
                .toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(News::fromJson)
                .take(3)
                .subscribe(ts);

        ts.awaitTerminalEvent(5, TimeUnit.SECONDS);
        List<News> messages = ts.getOnNextEvents();

        assertThat(messages, hasSize(3));

        for (News news : messages) {
            assertThat(news.getTitle(), is(not(isEmptyString())));
            assertThat(news.getNews(), is(not(isEmptyString())));
        }
    }
}