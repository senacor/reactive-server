package com.senacor.reactile.service.customer;

import com.senacor.reactile.hystrix.interception.HystrixCmd;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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
    public void getCustomer(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler) {
        Rx.bridgeHandler(getCustomer(customerId), resultHandler);
    }

    @HystrixCmd(CustomerServiceImplGetCustomerCommand.class)
    public Observable<Customer> getCustomer(CustomerId customerId) {
        return mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson);
    }

    public void getCustomer_differentApproach(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler) {
        mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson)
                .subscribe(Rx.toSubscriber(resultHandler));
    }

    @Override
    public void createCustomer(Customer customer, Handler<AsyncResult<Customer>> resultHandler) {
        JsonObject cust = customer.toJson().put("_id", customer.getId().toValue());
        Rx.bridgeHandler(
                mongoService.insertObservable(COLLECTION, cust)
                        .flatMap(res -> Observable.just(customer))
                , resultHandler);
    }

    @Override
    public void updateAddress(CustomerId customerId, Address address, Handler<AsyncResult<Customer>> resultHandler) {
        updateAddress(customerId, address).subscribe(Rx.toSubscriber(resultHandler));
    }

    @HystrixCmd(CustomerServiceImplUpdateAddressCommandFactory.class)
    public Observable<Customer> updateAddress(CustomerId customerId, Address address) {
        return mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson) // 2. convert json to Objects
                .map(customer -> Customer.addOrReplaceAddress(customer, address))
                .flatMap(customer -> {
                    // 3. execute mongo update
                    JsonObject update = new JsonObject().put("$set", new JsonObject().put("addresses",
                            marshal(customer.getAddresses(), Address::toJson)));
                    JsonObject query = new JsonObject().put("id", customerId.getId());
                    return mongoService.updateObservable(COLLECTION, query, update)
                            .flatMap(res -> Observable.just(customer));
                })
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

    @Override
    public void updateContact(CustomerId customerId, Contact address, Handler<AsyncResult<Customer>> resultHandler) {
        //TODO mmenzel
    }
}
