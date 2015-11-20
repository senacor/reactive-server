package com.senacor.reactile.domain;

import static com.senacor.reactile.domain.HttpResponseMatchers.hasHeader;
import static com.senacor.reactile.domain.HttpResponseMatchers.hasStatus;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperties;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.gateway.InitialDataVerticle;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.http.HttpResponse;
import com.senacor.reactile.http.HttpTestClient;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.service.branch.Branch;
import com.senacor.reactile.service.branch.BranchServiceImpl;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.Country;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerFixtures;
import com.senacor.reactile.service.customer.CustomerService;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;


public class GatewayVerticleTest {

    private static final Logger logger = LoggerFactory.getLogger(GatewayVerticleTest.class);

    @ClassRule
    public static final VertxRule vertxRule = new VertxRule(Services.GatewayService).deployVerticle(InitialDataVerticle.class);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private CustomerService service;

    private final HttpTestClient httpClient = new HttpTestClient(Vertx.vertx());
    
	@Rule
	public final MongoInitializer initializer = new MongoInitializer(vertxRule.vertx(), BranchServiceImpl.COLLECTION);

	@Before
	public void init() {		
		initializer.writeBlocking(Branch.newBuilder("1").withName("Bonn").withAddress(new Address("Bla", "Foo Str.", "12345", "6", "Bonn", new Country("Germany", "DE"), 1)).build());
	}

    @Test
    public void thatRequestsAreHandled() throws Exception {
        HttpResponse response = httpClient.get("/start?user=momann&customerId=cust-100000&branchId=1");
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
}