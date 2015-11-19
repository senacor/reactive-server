package com.senacor.reactile.service.customer;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;

import static com.senacor.reactile.json.JsonObjects.marshal;

public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public static final String COLLECTION = "customers";
    private final MongoService mongoService;
    private final Vertx vertx;

    @Inject
    public CustomerServiceImpl(MongoService mongoService, Vertx vertx) {
        this.mongoService = mongoService;
        this.vertx = vertx;
    }

    @Override
    public Observable<Customer> getCustomer(CustomerId customerId) {
        return mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson);
    }

    @Override
    public Observable<Customer> createCustomer(Customer customer) {
        JsonObject cust = customer.toJson().put("_id", customer.getId().toValue());
        return mongoService.insertObservable(COLLECTION, cust)
               .flatMap(res -> Observable.just(customer));
    }

    @Override
    public Observable<Customer> updateAddress(CustomerId customerId, Address address) {
        return mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson) // 2. convert json to Objects
                .map(customer -> Customer.addOrReplaceAddress(customer, address))
                .flatMap(this::updateAllAddressesInMongo)
                .doOnNext(customer -> {
                    // 4. publish 'updateAddress' Event
                    String eventAddress = CustomerService.ADDRESS_EVENT_UPDATE_ADDRESS;
                    logger.info("publishing on '" + eventAddress + "'...");
                    vertx.eventBus().publish(eventAddress, CustomerAddressChangedEvt.newBuilder()
                            .withId(customerId)
                            .withNewAddress(address)
                            .build()
                            .toJson());
                    logger.info("publishing on '" + eventAddress + "' done");
                })
                .doOnError(throwable -> logger.error("updateAddress error", throwable));
    }

    private Observable<Customer> updateAllAddressesInMongo(Customer customer) {

        JsonObject update = new JsonObject().put("$set", new JsonObject().put("addresses",
                marshal(customer.getAddresses(), Address::toJson)));
        JsonObject query = new JsonObject().put("id", customer.getId().getId());

        return mongoService.updateObservable(COLLECTION, query, update)
                .flatMap(res -> Observable.just(customer));
    }

    @Override
    public Observable<Customer> updateContact(CustomerId customerId, Contact address) {
        return null;
    }
}
