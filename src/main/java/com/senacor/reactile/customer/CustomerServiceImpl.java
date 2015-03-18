package com.senacor.reactile.customer;

import com.senacor.reactile.mongo.ObservableMongoService;
import com.senacor.reactile.user.UserId;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;

import static com.senacor.reactile.customer.Address.anAddress;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;


public class CustomerServiceImpl implements CustomerService {

    public static final String COLLECTION = "customers";
    private final ObservableMongoService mongoService;

    @Inject
    public CustomerServiceImpl(ObservableMongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void getCustomer(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler) {
        mongoService.findOne(COLLECTION, customerId.toJson(), null, result -> {
            if (result.failed()) {
                resultHandler.handle(failedFuture(result.cause()));
            } else
                resultHandler.handle(succeededFuture(Customer.fromJson(result.result())));
        });
    }

    @Override
    public void createCustomer(Customer customer, Handler<AsyncResult<Customer>> resultHandler) {
        JsonObject cust = customer.toJson().put("_id", customer.getId().toValue());
        mongoService.insert(COLLECTION, customer.toJson(), result -> {
            if (result.failed()) {
                resultHandler.handle(failedFuture(result.cause().getMessage()));
            } else
                resultHandler.handle(succeededFuture(customer));
        });
    }


    private CustomerAddressChangedEvt newCustomerChangedEvent() {
        return new CustomerAddressChangedEvt(new UserId("momann"), new CustomerId("007"), anAddress()
                .withStreet("Erika-Mann-Straße")
                .withAddressNumber("55")
                .withCountry(new Country("DE", "Deutschland"))
                .build());
    }

}
