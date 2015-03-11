package com.senacor.reactile.customer;

import com.senacor.reactile.gateway.GatewayVerticle;
import com.senacor.reactile.mongo.ObservableMongoService;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import com.senacor.reactile.user.UserId;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import rx.Observable;

import static com.senacor.reactile.customer.Address.anAddress;

public class CustomerServiceVerticle extends AbstractServiceVerticle {

    public static final String ADDRESS = "CustomerServiceVerticle";

    private ObservableMongoService mongoService;
    private String collection;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        collection = context.config().getString("collection");
    }

    @Override
    public void start() throws Exception {
        super.start();
        //TODO configuration and guice injection
        MongoService eventBusProxy = MongoService.createEventBusProxy(getVertx(), "vertx.mongo");
        mongoService = ObservableMongoService.from(eventBusProxy);

        vertx.setPeriodic(1000, tick -> vertx.eventBus().publish(GatewayVerticle.PUBLISH_ADDRESS, newCustomerChangedEvent()));
    }



    @Action
    public Observable<Customer> getCustomer(CustomerId id) {
        JsonObject query = new JsonObject().put("id", id.toValue());
        return mongoService.findOne(collection, query).map(Customer::fromJson);
    }

    @Action("add")
    public Observable<Customer> addCustomer(Customer customer) {
        JsonObject customerJson = customer.toJson().put("_id", customer.getId().toValue());
        return mongoService.insert(collection, customerJson).flatMap(id -> Observable.just(customer));
    }

    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("DE", "Deutschland"))
                .build());
    }

}
