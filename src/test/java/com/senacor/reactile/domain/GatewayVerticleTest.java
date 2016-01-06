package com.senacor.reactile.domain;

import com.google.common.base.Throwables;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.gateway.InitialDataVerticle;
import com.senacor.reactile.gateway.PushNotificationVerticle;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.http.HttpResponse;
import com.senacor.reactile.http.HttpTestClient;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentCreatedOrUpdatedEvt;
import com.senacor.reactile.service.appointment.AppointmentService;
import com.senacor.reactile.service.customer.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import javax.inject.Inject;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.senacor.reactile.domain.HttpResponseMatchers.hasHeader;
import static com.senacor.reactile.domain.HttpResponseMatchers.hasStatus;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperties;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasSize;
import static org.junit.Assert.*;


public class GatewayVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(GatewayVerticleTest.class);

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.GatewayService).deployVerticle(InitialDataVerticle.class);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private CustomerService service;

    @Inject
    private AppointmentService appointmentService;

    private final HttpTestClient httpClient = new HttpTestClient(Vertx.vertx());

    @Test
    public void thatRequestsAreHandled() throws Exception {
        HttpResponse response = httpClient.get("/start?user=momann&customerId=cust-100000");
        assertThat(response, hasStatus(200));
        logger.info("header: " + response.headersAsString());
        assertThat(response, hasHeader("content-length"));
        assertThat(response, hasHeader("Access-Control-Allow-Origin", "*"));
        assertThat(response, hasHeader("x-response-time"));

        JsonObject json = response.asJson();
        logger.info("response json: " + json.encodePrettily());

        assertThat(json, hasProperties("customer", "customer"));
        JsonObject jsonCustomer = json.getJsonObject("customer");
        assertThat(jsonCustomer, hasProperties("products", "transactions"));
        assertThat(jsonCustomer.getJsonObject("products"), hasProperties("accounts", "creditCards"));

        JsonObject products = jsonCustomer.getJsonObject("products");
        JsonArray accounts = products.getJsonArray("accounts");
        assertThat("accounts", accounts, hasSize(1));

        JsonArray creditCards = products.getJsonArray("creditCards");
        assertThat("creditCards", creditCards, hasSize(1));

        assertThat(creditCards, hasSize(1));

        assertThat(json, hasProperties("branch", "branch"));
        assertThat(json, hasProperties("appointments", "appointments"));
    }

    @Test
    public void thatGetBranchWithUsers() throws Exception {
        HttpResponse response = httpClient.get("/branchWithUsers?branchId=1");
        assertThat(response, hasStatus(200));
        logger.info("header: " + response.headersAsString());
        assertThat(response, hasHeader("content-length"));
        assertThat(response, hasHeader("Access-Control-Allow-Origin", "*"));
        assertThat(response, hasHeader("x-response-time"));

        JsonObject json = response.asJson();
        logger.info("response json: " + json.encodePrettily());

        assertThat(json, hasProperties("branch", "users"));

        JsonObject branch = json.getJsonObject("branch");
        assertThat(branch, hasProperties("id", "address"));
        JsonObject address = branch.getJsonObject("address");
        assertThat(address, hasProperties("street", "zipCode"));

    }

    @Test
    public void testUpdateCustomerAddress() throws Exception {
        // create customer
        Customer customer = CustomerFixtures.randomCustomer();
        customer = service.createCustomer(customer).toBlocking().first();
        Address newAddress = Address.anAddress()
                .withAddress(customer.getAddresses().get(0))
                .withZipCode("00815")
                .withCity("NewCity")
                .build();

        // update address via HTTP endpoint
        updateAddress(customer, newAddress);
    }

    @Test
    @Ignore("only for manual tests (receive messages on client eventbus)")
    public void testUpdateCustomerAddressAndSleep() throws Exception {
        // create customer
        Customer customer = CustomerFixtures.randomCustomer("cust-0815");
        customer = service.createCustomer(customer).toBlocking().first();
        Address newAddress = Address.anAddress()
                .withAddress(customer.getAddresses().get(0))
                .withZipCode("00815")
                .withCity("NewCity")
                .build();

        for (int i = 0; i < 100; i++) {
            // update address via HTTP endpoint
            updateAddress(customer, newAddress);
            logger.info("sleeping...");
            Thread.sleep(5000);
        }
    }

    private void updateAddress(Customer customer, Address newAddress) throws Exception {
        HttpResponse response = httpClient.put("/customer/" + customer.getId().getId() + "/addresses"
                , newAddress);
        logger.info("header: " + response.headersAsString());
        assertThat(response, hasStatus(200));
        assertThat(response, hasHeader("content-length"));
        assertThat(response, hasHeader("Access-Control-Allow-Origin", "*"));
        assertThat(response, hasHeader("x-response-time"));

        logger.info("response.body: " + response.getBody());
        logger.info("response.statusMessage: " + response.statusMessage());
        JsonObject json = response.asJson();
        logger.info("response json: " + json.encodePrettily());
        Customer customerUpdated = Customer.fromJson(json);
        assertThat("customer.addresses", customerUpdated.getAddresses(), Matchers.hasSize(1));
        assertEquals(newAddress.getCity(), customerUpdated.getAddresses().get(0).getCity());
    }

    @Test @Ignore("Only for manual tests")
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

        Observable<Long> timer = Observable.timer(1000, 1000, TimeUnit.MILLISECONDS);

        // create customer and update Address
        timer.withLatestFrom(service.createCustomer(customer), (t, cust) -> cust)
                .map(customerCreated -> Address.anAddress()
                        .withAddress(customerCreated.getAddresses().get(0))
                        .withZipCode("00815")
                        .withCity("NewCity")
                        .build())
                .flatMap(newAddress -> service.updateAddress(customer.getId(), newAddress))
                .subscribe(customerWithUpdatedAddress -> logger.info("updateAddress: " + customerWithUpdatedAddress));

        CustomerAddressChangedEvt event = queue.poll(5L, TimeUnit.SECONDS);
        logger.info("Received Event: " + event);
        assertNotNull("CustomerAddressChangedEvt not received", event);
        assertEquals("event.newAddress.city", "NewCity", event.getNewAddress().getCity());

        Thread.sleep(100000);
        logger.info("Received Events: " + queue.size());
    }

    @Test //@Ignore("Only for manual tests")
    public void testAppointmentCreateOrUpdateEvent() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();

        final LinkedBlockingQueue<AppointmentCreatedOrUpdatedEvt> queue = new LinkedBlockingQueue<>();

        // listen for events
        String eventAddress = PushNotificationVerticle.PUBLISH_ADDRESS_APPOINTMENT_CREATE_OR_UPDATE + customer.getId().getId();
        logger.info("listening on address '" + eventAddress + "'");
        vertxRule.eventBus().consumer(eventAddress)
                .toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(AppointmentCreatedOrUpdatedEvt::fromJson)
                .subscribe(queue::add,
                        throwable -> fail(throwable.getMessage() + Throwables.getStackTraceAsString(throwable)));

        Observable<Long> timer = Observable.timer(1000, 1000, TimeUnit.MILLISECONDS);

        // create customer and update Address
        timer.withLatestFrom(service.createCustomer(customer), (t, cust) -> cust)
                .map(customerCreated -> Appointment.newBuilder()
                        .withId("99")
                        .withCustomerId(customerCreated.getId().getId())
                        .build())
                .flatMap(newAppointment -> appointmentService.createOrUpdateAppointment(newAppointment))
                .subscribe(appointment -> logger.info("create appointment: " + appointment));

        AppointmentCreatedOrUpdatedEvt event = queue.poll(5L, TimeUnit.SECONDS);
        logger.info("Received Event: " + event);
        //assertNotNull("AppointmentCreatedOrUpdatedEvt not received", event);

        Thread.sleep(100000);
        logger.info("Received Events: " + queue.size());
    }
}