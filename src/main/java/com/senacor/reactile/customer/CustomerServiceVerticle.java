package com.senacor.reactile.customer;

import com.senacor.reactile.user.UserId;
import com.senacor.reactile.gateway.GatewayVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import rx.Observable;

import static com.senacor.reactile.customer.Address.anAddress;

public class CustomerServiceVerticle extends AbstractVerticle {


    public static final String ADDRESS = "customer";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MongoService mongoService;

    @Override
    public void start() throws Exception {
        mongoService = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");

        EventBus eventBus = vertx.eventBus();

        MessageConsumer<CustomerId> consumer = eventBus.consumer(ADDRESS);
        consumer.toObservable().subscribe(message -> getCustomer(message.body()).subscribe(message::reply));

        vertx.setPeriodic(1000, tick -> eventBus.publish(GatewayVerticle.PUBLISH_ADDRESS, newCustomerChangedEvent()));
    }


    private Observable<Customer> getCustomer(CustomerId id) {
        ObservableFuture<JsonObject> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("id", id.toValue());
        mongoService.findOne("customers", query, null, observable.asHandler());

        return observable.map(Customer::fromJson);
    }

    @Override
    public void stop() throws Exception {

    }

    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("DE", "Deutschland"))
                .build());
    }
}
