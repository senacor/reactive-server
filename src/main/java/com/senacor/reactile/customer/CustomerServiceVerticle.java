package com.senacor.reactile.customer;

import com.senacor.reactile.mongo.ObservableMongoService;
import com.senacor.reactile.service.AbstractServiceVerticle;
import com.senacor.reactile.service.Action;
import com.senacor.reactile.user.UserId;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import javax.inject.Inject;

import static com.senacor.reactile.customer.Address.anAddress;


public class CustomerServiceVerticle extends AbstractServiceVerticle {

    public static final String ADDRESS = "CustomerServiceVerticle";

    private final ObservableMongoService mongoService;
    private String collection;

    @Inject
    public CustomerServiceVerticle(ObservableMongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        collection = context.config().getString("collection");
    }

    @Action
    public Observable<Customer> getCustomer(CustomerId id) {
        JsonObject query = new JsonObject().put("id", id.toValue());
        return mongoService.findOne(collection, query).map(Customer::fromJson);
    }

    @Action("add")
    public Observable<Customer> addCustomer(Customer customer) {
        return mongoService.insert(collection, customer.toJson()).flatMap(id -> Observable.just(customer));
    }

    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("DE", "Deutschland"))
                .build());
    }

}
