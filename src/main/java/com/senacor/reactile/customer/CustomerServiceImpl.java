package com.senacor.reactile.customer;

import com.senacor.reactile.rx.Rx;
import com.senacor.reactile.user.UserId;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;

import static com.senacor.reactile.customer.Address.anAddress;
import static com.senacor.reactile.customer.Customer.addOrReplaceAddress;
import static com.senacor.reactile.json.JsonObjects.marshal;


public class CustomerServiceImpl implements CustomerService {

    public static final String COLLECTION = "customers";
    private final MongoService mongoService;

    @Inject
    public CustomerServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getCustomer(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler) {
        Rx.bridgeHandler(mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson), resultHandler);
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
        // 1. load customer from Database
        mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson) // convert json to Objects
                .map(customer -> addOrReplaceAddress(customer, address))
                .flatMap(customer -> {
                    // execute mongo update
                    JsonObject update = new JsonObject().put("$set", new JsonObject().put("addresses",
                            marshal(customer.getAddresses(), Address::toJson)));
                    JsonObject query = new JsonObject().put("id", customerId.getId());
                    return mongoService.updateObservable(COLLECTION, query, update)
                            .flatMap(res -> Observable.just(customer));
                })
                .subscribe(Rx.toSubscriber(resultHandler));
    }

    @Override
    public void updateContact(CustomerId customerId, Contact address, Handler<AsyncResult<Customer>> resultHandler) {
        //TODO mmenzel
    }


    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("Deutschland", "DE"))
                .withIndex(1)
                .build());
    }

}
