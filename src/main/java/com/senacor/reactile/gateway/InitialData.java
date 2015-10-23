package com.senacor.reactile.gateway;

import com.senacor.reactile.rxjava.service.account.AccountService;
import com.senacor.reactile.rxjava.service.account.TransactionService;
import com.senacor.reactile.rxjava.service.creditcard.CreditCardService;
import com.senacor.reactile.rxjava.service.customer.CustomerService;
import com.senacor.reactile.rxjava.service.user.UserService;
import com.senacor.reactile.service.account.Account;
import com.senacor.reactile.service.account.AccountFixtures;
import com.senacor.reactile.service.account.Product;
import com.senacor.reactile.service.account.Transaction;
import com.senacor.reactile.service.account.TransactionFixtures;
import com.senacor.reactile.service.branch.Branch;
import com.senacor.reactile.service.creditcard.CreditCard;
import com.senacor.reactile.service.creditcard.CreditCardFixtures;
import com.senacor.reactile.service.customer.CustomerFixtures;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.User;

import com.senacor.reactile.service.user.UserFixtures;
import com.senacor.reactile.service.user.UserId;
import rx.Observable;
import rx.Scheduler;

import javax.inject.Inject;
import java.util.Random;

public class InitialData {

    private final Scheduler scheduler;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final Random rn = new Random();

    @Inject
    public InitialData(Scheduler scheduler, CustomerService customerService, AccountService accountService,
                       CreditCardService creditCardService, TransactionService transactionService, UserService userService) {
        this.scheduler = scheduler;
        this.customerService = customerService;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    Observable<CustomerId> initialize(Observable<CustomerId> customerIds) {
        return customerIds
                .observeOn(scheduler)
                .map(CustomerFixtures::randomCustomer)
                .flatMap(customer -> customerService.createCustomerObservable(customer))
                .map(customer -> AccountFixtures.randomAccount(customer.getId()))
                .flatMap(account -> createAccountWithTransactions(account))
                .map(account -> CreditCardFixtures.randomCreditCard(account.getCustomerId()))
                .flatMap(creditCard -> createCreditCardWithTransactions(creditCard))
                .map(cc -> cc.getCustomerId());

    }

    Observable<UserId> initializeUser(Observable<UserId> userIDs){
        return userIDs.flatMap(userId -> createUser(userId, randomBranchId()))
                .map(user -> user.getId());

    }


    private Observable<CreditCard> createCreditCardWithTransactions(CreditCard creditCard) {
        return withTransactions(creditCardService.createCreditCardObservable(creditCard))
                .flatMap(transaction -> Observable.just(creditCard));
    }

    private Observable<Account> createAccountWithTransactions(Account account) {
        return withTransactions(accountService.createAccountObservable(account))
                .flatMap(transaction -> Observable.just(account));
    }

    private Observable<User> createUser(UserId userId, String branch){
        return userService.createUserObservable(UserFixtures.createUser(userId, branch));

    }


    private Observable<Transaction> withTransactions(Observable<? extends Product> productObservable) {
        return productObservable
                .flatMap(p -> TransactionFixtures.randomTransactions(p.getCustomerId(), p, rn.nextInt(12) + 3))
                .flatMap(transaction -> transactionService.createTransactionObservable(transaction))
                .last()
                ;
    }

    private String randomBranchId(){
        return "1";
    }

}
