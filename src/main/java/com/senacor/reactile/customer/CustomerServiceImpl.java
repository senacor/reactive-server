package com.senacor.reactile.customer;

import com.senacor.reactile.rx.Rx;
import com.senacor.reactile.user.UserId;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

import static com.senacor.reactile.customer.Address.anAddress;
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
    public void updateAddress(CustomerId customerId, Address address, Handler<AsyncResult<Void>> resultHandler) {
        // 1. load customer from Database
        mongoService.findOneObservable(COLLECTION, customerId.toJson(), null)
                .map(Customer::fromJson) // convert json to Objects
                .map(customer -> addOrReplaceAddress(customer, address))
                .map(Customer::getAddresses) // get addresses
                .map(addresses -> marshal(addresses, Address::toJson)) // convert addresses to json
                .map(addresses -> new JsonObject().put("$set", new JsonObject().put("addresses", addresses))) // create update json
                .flatMap(update -> {
                    // execute mongo update
                    JsonObject query = new JsonObject().put("id", customerId.getId());
                    return mongoService.updateObservable(COLLECTION, query, (JsonObject) update);
                })
                .subscribe(Rx.toSubscriber(resultHandler));
    }

    /**
     * @param customer   Customer
     * @param newAddress new Address
     * @return Customer with replaced od added Address
     */
    private Customer addOrReplaceAddress(Customer customer, Address newAddress) {
        customer.getAddresses().stream()
                .filter(address -> Objects.equals(newAddress.getIndex(), address.getIndex()))
                .findFirst()
                .map(address -> customer.getAddresses().remove(address));
        customer.getAddresses().add(newAddress);
        return customer;
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
