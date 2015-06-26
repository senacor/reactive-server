package com.senacor.reactile.gateway;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerFixtures;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.http.HttpResponse;
import com.senacor.reactile.http.HttpTestClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.senacor.reactile.domain.HttpResponseMatchers.hasHeader;
import static com.senacor.reactile.domain.HttpResponseMatchers.hasStatus;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperties;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GatewayVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(GatewayVerticleTest.class);

    @Rule
    public final VertxRule vertxRule = new VertxRule(Services.GatewayService).deployVerticle(InitialDataVerticle.class);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.customer.CustomerService service;

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

        assertThat(json, hasProperties("customer", "branch", "appointments", "recommendations", "news"));
        JsonObject jsonCustomer = json.getJsonObject("customer");
        assertThat(jsonCustomer, hasProperties("products", "transactions"));
        assertThat(jsonCustomer.getJsonObject("products"), hasProperties("accounts", "creditCards"));

        JsonObject products = jsonCustomer.getJsonObject("products");
        JsonArray accounts = products.getJsonArray("accounts");
        assertThat("accounts", accounts, hasSize(1));
        JsonArray creditCards = products.getJsonArray("creditCards");
        assertThat("creditCards", creditCards, hasSize(1));
        JsonArray appointments = json.getJsonArray("appointments");
        assertThat("appointments", appointments, hasSize(2));
    }

    @Test
    public void testUpdateCustomerAddress() throws Exception {
        // create customer
        Customer customer = CustomerFixtures.randomCustomer();
        customer = service.createCustomerObservable(customer).toBlocking().first();
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
        customer = service.createCustomerObservable(customer).toBlocking().first();
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
}