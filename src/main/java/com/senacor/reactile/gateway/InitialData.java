package com.senacor.reactile.gateway;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.AccountFixtures;
import com.senacor.reactile.account.AccountService;
import com.senacor.reactile.creditcard.CreditCard;
import com.senacor.reactile.creditcard.CreditCardFixtures;
import com.senacor.reactile.creditcard.CreditCardService;
import com.senacor.reactile.account.TransactionFixtures;
import com.senacor.reactile.account.TransactionService;
import com.senacor.reactile.customer.CustomerFixtures;
import com.senacor.reactile.customer.CustomerId;
import com.senacor.reactile.customer.CustomerService;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;
import rx.Scheduler;

import javax.inject.Inject;
import java.util.Random;

public class InitialData {

    private final Vertx vertx;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final TransactionService transactionService;
    private final Random rn = new Random();

    @Inject
    public InitialData(Vertx vertx, CustomerService customerService, AccountService accountService, CreditCardService creditCardService, TransactionService transactionService) {
        this.vertx = vertx;
        this.customerService = customerService;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.transactionService = transactionService;
    }

    Observable<CustomerId> initialize(Observable<CustomerId> customerIds) {
        Scheduler scheduler = RxHelper.scheduler(vertx);
        return customerIds
                .map(CustomerFixtures::randomCustomer)
                .observeOn(scheduler)
                .flatMap(customer -> customerService.createCustomer(customer))
                .map(customer -> AccountFixtures.randomAccount(customer.getId()))
                .flatMap(account -> createAccountWithTransactions(account))
                .map(account -> CreditCardFixtures.randomCreditCard(account.getCustomerId()))
                .flatMap(creditCard -> createCreditCardWithTransactions(creditCard))
                .map(cc -> cc.getCustomerId());

    }

    private Observable<CreditCard> createCreditCardWithTransactions(CreditCard creditCard) {
        return creditCardService.createCreditCard(creditCard)
                .flatMap(cc -> TransactionFixtures.randomTransactions(cc.getCustomerId(), cc.getId(), rn.nextInt(12) + 3))
                .flatMap(transaction -> transactionService.createTransaction(transaction))
                .last()
                .flatMap(transaction -> Observable.just(creditCard));
    }

    private Observable<Account> createAccountWithTransactions(Account account) {
        return accountService.createAccount(account)
                .flatMap(acc -> TransactionFixtures.randomTransactions(acc.getCustomerId(), acc.getId(), rn.nextInt(12) + 3))
                .flatMap(transaction -> transactionService.createTransaction(transaction))
                .last()
                .flatMap(transaction -> Observable.just(account));
    }

}
