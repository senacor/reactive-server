package com.senacor.reactile.customer;

import com.senacor.reactile.EventBusRule;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.bootstrap.ApplicationStartup;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CustomerServiceVerticleTest {

    @Rule
    public final VertxRule vertxRule = new VertxRule(ApplicationStartup.class);
    {
        vertxRule.deployVerticle(Services.CustomerService);
    }

    @Rule
    public final EventBusRule eventBusRule = new EventBusRule(vertxRule.vertx());

    @Test
    public void thatVerticleRespondsToMessage() throws InterruptedException, ExecutionException, TimeoutException {
        CustomerId customerId = new CustomerId("08-cust-15");
        Message<Customer> customer = eventBusRule.sendObservable(CustomerServiceVerticle.ADDRESS, customerId, "getCustomer");
        assertThat(customer.body().getId(), is(equalTo(customerId)));
    }

    @Test
    public void fillWithTestdata() {
        MongoService service = MongoService.createEventBusProxy((Vertx) vertxRule.vertx().getDelegate(), "vertx.mongo");

        Observable<Customer> testCustomers = Observable.zip(
                addressNumber(),
                firstName(),
                lastName(),
                streetName(),
                streetType(), (addrNum, fname, lname, sname, stype) -> {
                    Address addr = Address.anAddress()
                            .withAddressNumber("addr-"+addrNum)
                            .withStreet(sname+stype)
                            .withCoHint("")
                            .withCity("Nürnberg")
                            .withZipCode("12345")
                            .withCountry(new Country("Deutschland", "DE")).build();

                    return Customer.newBuilder()
                            .withId("cust-"+addrNum)
                            .withFirstname(fname)
                            .withLastname(lname)
                            .withAddresses(Arrays.asList(addr))
                            .withTaxNumber("tax-"+addrNum)
                            .withTaxCountry(new Country("Deutschland", "DE"))
                            .build();
                }
        );




        testCustomers.flatMap(customer -> {
            ObservableFuture<String> newOne = RxHelper.observableFuture();
            service.insertWithOptions("customers", customer.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());
            return newOne;
        }).subscribe(outcome -> {
            System.out.println("outcome = " + outcome);
        }, Throwable::printStackTrace,()-> {
            System.out.println("done!!!!");
        });
    }


    private Observable<Integer> addressNumber() {
        return Observable.range(1000, 100000);
    }

    private Observable<String> streetName() {
        List streets = Arrays.asList("Winter", "Frühling", "Sommer", "Herbst", "Amsel", "Drossel", "Fink", "Star");
        return Observable.from(streets).repeat();
    }

    private Observable<String> streetType() {
        List streets = Arrays.asList("strasse", "weg", "pfad");
        return Observable.from(streets).repeat();
    }

    private Observable<String> firstName() {
        List streets = Arrays.asList("Adam", "Anneliese", "Berthold", "Berta", "Christopher", "Charlotte", "Dennis", "Dorothea");
        return Observable.from(streets).repeat();
    }

    private Observable<String> lastName() {
        List streets = Arrays.asList("Kugler", "Lurchig", "Monheim", "Naaber", "Peine", "Quaid", "Rastatt");
        return Observable.from(streets).repeat();
    }

}