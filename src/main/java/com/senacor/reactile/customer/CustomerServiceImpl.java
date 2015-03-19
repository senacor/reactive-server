package com.senacor.reactile.customer;

import com.senacor.reactile.rx.Rx;
import com.senacor.reactile.service.Action;
import com.senacor.reactile.user.UserId;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;

import static com.senacor.reactile.customer.Address.anAddress;


public class CustomerServiceImpl implements CustomerService {

    public static final String COLLECTION = "customers";
    private final MongoService mongoService;

    @Inject
    public CustomerServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getCustomer(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler) {
        Rx.bridgeHandler(mongoService.findOneObservable(COLLECTION, customerId.toJson(), null).map(Customer::fromJson), resultHandler);
    }

    @Override
    public void createCustomer(Customer customer, Handler<AsyncResult<Customer>> resultHandler) {
        JsonObject cust = customer.toJson().put("_id", customer.getId().toValue());
        Rx.bridgeHandler(mongoService.insertObservable(COLLECTION, cust).flatMap(res -> Observable.just(customer)), resultHandler);
    }
/*
    @Override
    public void updateCustomerName(String customerId, String newName, Handler<AsyncResult<Void>> resultHandler) {
        JsonObject query = new JsonObject().put("id", customerId);
        JsonObject update = new JsonObject().put("$set",
                new JsonObject().put("lastname", newName));
        Rx.bridgeHandler(mongoService.updateObservable(COLLECTION, query, update), resultHandler);
    }*/

    @Override
    public void updateAddress(CustomerId customerId, Address address, Handler<AsyncResult<Void>> resultHandler) {
        JsonObject query = new JsonObject().put("id", customerId.getId());
        JsonObject update = new JsonObject().put("$set",
                new JsonObject().put("addresses", new JsonArray().add(address.toJson())));
        Rx.bridgeHandler(mongoService.updateObservable(COLLECTION, query, update), resultHandler);
    }

    @Override
    public void updateContact(CustomerId customerId, Contact address, Handler<AsyncResult<Customer>> resultHandler) {
        //TODO mmenzel
    }


    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("DE", "Deutschland"))
                .build());
    }

}
