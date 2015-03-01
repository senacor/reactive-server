package com.senacor.reactile.customer;

import com.senacor.reactile.gateway.GatewayVerticle;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import com.senacor.reactile.user.UserId;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

import static com.senacor.reactile.customer.Address.anAddress;

public class CustomerServiceVerticle extends AbstractServiceVerticle {

    public static final String ADDRESS = "CustomerServiceVerticle";

    private MongoService mongoService;

    @Override
    public void start() throws Exception {
        mongoService = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");

        vertx.setPeriodic(1000, tick -> vertx.eventBus().publish(GatewayVerticle.PUBLISH_ADDRESS, newCustomerChangedEvent()));
    }


    @Action
    private Observable<Customer> getCustomer(CustomerId id) {
        ObservableFuture<JsonObject> observable = RxHelper.observableFuture();

        JsonObject query = new JsonObject().put("id", id.toValue());
        mongoService.findOne("customers", query, null, observable.asHandler());

        return observable.map(Customer::fromJson);
    }

    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("DE", "Deutschland"))
                .build());
    }
}
