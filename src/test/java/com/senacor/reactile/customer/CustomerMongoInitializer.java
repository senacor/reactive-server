package com.senacor.reactile.customer;

import com.senacor.reactile.account.*;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.WriteOption;
import io.vertx.rx.java.ObservableFuture;
import io.vertx.rx.java.RxHelper;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.functions.Func5;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CustomerMongoInitializer {

    private final io.vertx.rxjava.core.Vertx vertx;
    private final Random rd = new Random();

    public CustomerMongoInitializer(io.vertx.rxjava.core.Vertx vertx) {
        this.vertx = vertx;
    }

    public void write(int count) {
        MongoService service = MongoService.createEventBusProxy((Vertx) vertx.getDelegate(), "vertx.mongo");

        Observable<Customer> testCustomers = Observable.zip(
                addressNumber(count),
                firstName(),
                lastName(),
                streetName(),
                streetType(), zipToCustomer()
        );

        testCustomers.flatMap(insertCustomerWithAccounts(service)).subscribe(
                outcome -> {
//                        System.out.println("outcome = " + outcome);
                    },
                    Throwable::printStackTrace,
                    () -> System.out.println("done!")
                );
    }

    private Func1<Customer, Observable<? extends String>> insertCustomer(MongoService service) {
        return customer -> {
            ObservableFuture<String> newOne = RxHelper.observableFuture();
            service.insertWithOptions("customers", customer.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());
            return newOne;
        };
    }

    private Func1<Customer, Observable<? extends String>> insertCustomerWithAccounts(MongoService service) {
        return customer -> {
            Observable<Account> testAccounts = Observable.zip(
                    accountId(customer.getId()),
                    Observable.just(customer.getId()).repeat(),
                    balance().repeat(),
                    zipToAccount()
            );

            Observable<CreditCard> testCreditCards = Observable.zip(
                    creditCardId(customer.getId()),
                    Observable.just(customer.getId()).repeat(),
                    balance().repeat(),
                    zipToCreditCard()
            );

            ObservableFuture<String> newOne = RxHelper.observableFuture();
//            System.err.println(">> inserting cust "+customer.getId());
            service.insertWithOptions("customers", customer.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());

            return testAccounts.flatMap(insertAccount(service)).mergeWith(testCreditCards.flatMap(insertCreditCard(service))).mergeWith(newOne);
        };
    }

    private Func1<Account, Observable<? extends String>> insertAccount(MongoService service) {
        return account -> {
            ObservableFuture<String> newOne = RxHelper.observableFuture();
//            System.err.println(">> inserting acc "+account.getId());
            service.insertWithOptions("accounts", account.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());

            Observable<Transaction> testAccTransactions = Observable.zip(
                    transactionId(account.getId()),
                    Observable.just(account.getCustomerId()).repeat(),
                    Observable.just(account.getId()).repeat(),
                    amount().repeat(),
                    zipToAccTransaction()
            );

            return newOne.mergeWith(testAccTransactions.flatMap(transaction -> {
                ObservableFuture<String> newTx = RxHelper.observableFuture();
//                System.err.println(">> inserting acc-tx "+transaction.getId());
                service.insertWithOptions("transactions", transaction.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());
                return newTx;
            }));
        };
    }

    private Func1<CreditCard, Observable<? extends String>> insertCreditCard(MongoService service) {
        return creditCard -> {
            ObservableFuture<String> newOne = RxHelper.observableFuture();
//            System.err.println(">> inserting cc "+creditCard.getId());
            service.insertWithOptions("creditcards", creditCard.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());

            Observable<Transaction> testCcTransactions = Observable.zip(
                    transactionId(creditCard.getId()),
                    Observable.just(creditCard.getCustomerId()).repeat(),
                    Observable.just(creditCard.getId()).repeat(),
                    amount().repeat(),
                    zipToCcTransaction()
            );

            return newOne.mergeWith(testCcTransactions.flatMap(transaction -> {
                ObservableFuture<String> newTx = RxHelper.observableFuture();
//                System.err.println(">> inserting cc-tx "+transaction.getId());
                service.insertWithOptions("transactions", transaction.toJson(), WriteOption.UNACKNOWLEDGED, newOne.asHandler());
                return newTx;
            }));
        };
    }

    private Func5<Integer, String, String, String, String, Customer> zipToCustomer() {
        return (addrNum, fname, lname, sname, stype) -> {
            Address addr = Address.anAddress()
                    .withAddressNumber("addr-" + addrNum)
                    .withStreet(sname + stype)
                    .withCoHint("")
                    .withCity("Nürnberg")
                    .withZipCode("12345")
                    .withCountry(new Country("Deutschland", "DE")).build();

            return Customer.newBuilder()
                    .withId("cust-" + addrNum)
                    .withFirstname(fname)
                    .withLastname(lname)
                    .withAddresses(Arrays.asList(addr))
                    .withTaxNumber("tax-" + addrNum)
                    .withTaxCountry(new Country("Deutschland", "DE"))
                    .build();
        };
    }

    private Observable<Integer> addressNumber(int count) {
        return Observable.range(1000, count);
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

    // ========

    private Observable<String> accountId(CustomerId customerId) {
        return Observable.range(1, rd.nextInt(4)+1).map(accId -> {
            return customerId.getId()+"-ac-"+accId;
        });
    }

    private Observable<String> creditCardId(CustomerId customerId) {
        return Observable.range(1, rd.nextInt(4)+1).map(ccId -> {
            return customerId.getId()+"-cc-"+ccId;
        });
    }

    private Observable<BigDecimal> balance() {
        return Observable.just(new BigDecimal(rd.nextInt(10000) - 5000));
    }

    private Func3<String, CustomerId, BigDecimal, Account> zipToAccount() {
        return (accountId, customerId, amount) -> {
            return Account.anAccount()
                    .withId(accountId)
                    .withCustomerId(customerId)
                    .withBalance(amount)
                    .withCurrency("EUR")
                    .build();
        };
    }

    private Func3<String, CustomerId, BigDecimal, CreditCard> zipToCreditCard() {
        return (accountId, customerId, amount) -> {
            return CreditCard.aCreditCard()
                    .withId(accountId)
                    .withCustomerId(customerId)
                    .withBalance(amount)
                    .withCurrency("EUR")
                    .build();
        };
    }

    // ========

    private Observable<String> transactionId(AccountId accountId) {
        return Observable.range(1, rd.nextInt(30)+1).map(txId -> {
            return accountId.getId()+"-tx-"+txId;
        });
    }

    private Observable<String> transactionId(CreditCardId creditCardId) {
        return Observable.range(1, rd.nextInt(30)+1).map(txId -> {
            return creditCardId.getId()+"-tx-"+txId;
        });
    }

    private Func4<String, CustomerId, AccountId, BigDecimal, Transaction> zipToAccTransaction() {
        return (transactionId, customerId, accountId, amount) -> {
            return Transaction.aTransaction()
                    .withId(transactionId)
                    .withCustomerId(customerId.getId())
                    .withAccountId(accountId.getId())
                    .withAmount(amount)
                    .withCurrency("EUR")
                    .build();
        };
    }

    private Func4<String, CustomerId, CreditCardId, BigDecimal, Transaction> zipToCcTransaction() {
        return (transactionId, customerId, creditCardId, amount) -> {
            return Transaction.aTransaction()
                    .withId(transactionId)
                    .withCustomerId(customerId.getId())
                    .withCreditCardId(creditCardId.getId())
                    .withAmount(amount)
                    .withCurrency("EUR")
                    .build();
        };
    }

    private Observable<BigDecimal> amount() {
        return Observable.just(new BigDecimal(rd.nextInt(1000) - 300));
    }
}
