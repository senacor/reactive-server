package com.senacor.reactile.service.customer;

import com.google.common.base.Throwables;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.senacor.reactile.service.customer.CustomerFixtures.randomCustomer;
import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class CustomerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.CustomerService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.customer.CustomerService service;

    private MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), "customers");

    @Test
    public void thatCustomerIsReturned() {
        mongoInitializer.writeBlocking(CustomerFixtures.newCustomer("cust-asdfghjk"));
        Customer customer = service.getCustomerObservable(new CustomerId("cust-asdfghjk")).toBlocking().first();
        assertThat(customer, hasId("cust-asdfghjk"));
    }

    @Test
    public void thatCustomerCanBeCreated() {
        Customer customer = service.createCustomerObservable(randomCustomer("cust-254")).toBlocking().first();
        assertThat(customer, hasId("cust-254"));
    }

    @Test
    public void thatCustomerAddressCanBeAdded() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();
        mongoInitializer.writeBlocking(customer);

        int newIndex = customer.getAddresses().get(0).getIndex() + 1;
        Address newAddress = new Address("", "Teststreet", "TestPLZ", "8", "Testcity", new Country("Deutschland", "DE"), newIndex);
        Customer customerUpdated = service.updateAddressObservable(customer.getId(), newAddress).toBlocking().first();

        assertThat(customerUpdated.getAddresses(), hasSize(1 + customer.getAddresses().size()));

        Customer customerLoaded = service.getCustomerObservable(customer.getId()).toBlocking().first();

        assertThat(customerLoaded.getAddresses(), hasSize(1 + customer.getAddresses().size()));
        Optional<Address> newAddressOptional = customerLoaded.getAddresses().stream()
                .filter(address -> newIndex == address.getIndex())
                .findFirst();
        assertTrue("new address not found", newAddressOptional.isPresent());
        assertEquals("city", newAddress.getCity(), newAddressOptional.get().getCity());
    }

    @Test
    public void thatCustomerAddressCanBeUpdated() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();
        mongoInitializer.writeBlocking(customer);

        Address newAddress = new Address("", "Teststreet", "TestPLZ", "8", "Testcity", new Country("Deutschland", "DE"), customer.getAddresses().get(0).getIndex());
        Customer customerUpdated = service.updateAddressObservable(customer.getId(), newAddress).toBlocking().first();
        assertThat(customerUpdated.getAddresses(), hasSize(customer.getAddresses().size()));

        Customer customerLoaded = service.getCustomerObservable(customer.getId()).toBlocking().first();

        assertThat(customerLoaded.getAddresses(), hasSize(customer.getAddresses().size()));
        assertEquals("address.index", newAddress.getIndex(), customerLoaded.getAddresses().get(0).getIndex());
        assertEquals("address.city", newAddress.getCity(), customerLoaded.getAddresses().get(0).getCity());
    }

    @Test
    public void testReceiveCustomerAddressChangedEvt() throws Exception {
        final LinkedBlockingQueue<CustomerAddressChangedEvt> queue = new LinkedBlockingQueue<>();

        // listen to events
        vertxRule.eventBus().consumer(CustomerService.ADDRESS_EVENT_UPDATE_ADDRESS)
                .toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(CustomerAddressChangedEvt::fromJson)
                .doOnError(throwable -> fail(throwable.getMessage() + Throwables.getStackTraceAsString(throwable)))
                .subscribe(queue::add);

        // send event ...
        thatCustomerAddressCanBeUpdated();

        CustomerAddressChangedEvt event = queue.poll(2L, TimeUnit.SECONDS);
        logger.info("Received Event: " + event);
        assertNotNull("No Event Received", event);
        assertNotNull("event.id must not be null", event.getId());
        assertNotNull("event.newAddress must not be null", event.getNewAddress());
        assertNull("event.userId must be null", event.getUserId());
    }
}