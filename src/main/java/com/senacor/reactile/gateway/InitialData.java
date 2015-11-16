package com.senacor.reactile.gateway;

import com.senacor.reactile.service.account.*;
import com.senacor.reactile.service.branch.BranchDatabase;
import com.senacor.reactile.service.creditcard.CreditCard;
import com.senacor.reactile.service.creditcard.CreditCardFixtures;
import com.senacor.reactile.service.creditcard.CreditCardService;
import com.senacor.reactile.service.customer.CustomerFixtures;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.customer.CustomerService;
import com.senacor.reactile.service.user.User;
import com.senacor.reactile.service.user.UserFixtures;
import com.senacor.reactile.service.user.UserId;
import com.senacor.reactile.service.user.UserService;
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
    private final BranchDatabase branchDatabase;

    @Inject
    public InitialData(Scheduler scheduler, CustomerService customerService, AccountService accountService,
                       CreditCardService creditCardService, TransactionService transactionService, UserService userService, BranchDatabase branchDatabase) {
        this.scheduler = scheduler;
        this.customerService = customerService;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.branchDatabase = branchDatabase;
    }

    Observable<CustomerId> initialize(Observable<CustomerId> customerIds) {
        return customerIds
                .observeOn(scheduler)
                .map(CustomerFixtures::randomCustomer)
                .flatMap(customer -> customerService.createCustomer(customer))
                .map(customer -> AccountFixtures.randomAccount(customer.getId()))
                .flatMap(account -> createAccountWithTransactions(account))
                .map(account -> CreditCardFixtures.randomCreditCard(account.getCustomerId()))
                .flatMap(creditCard -> createCreditCardWithTransactions(creditCard))
                .map(cc -> cc.getCustomerId());

    }

    Observable<UserId> initializeUser(Observable<UserId> userIDs){
        return userIDs.flatMap(userId -> createUser(userId, branchDatabase.randomExistingID()))
                .map(user -> user.getId());

    }


    private Observable<CreditCard> createCreditCardWithTransactions(CreditCard creditCard) {
        return withTransactions(creditCardService.createCreditCard(creditCard))
                .flatMap(transaction -> Observable.just(creditCard));
    }

    private Observable<Account> createAccountWithTransactions(Account account) {
        return withTransactions(accountService.createAccount(account))
                .flatMap(transaction -> Observable.just(account));
    }

    private Observable<User> createUser(UserId userId, String branch){
        return userService.createUser(UserFixtures.createUser(userId, branch));

    }


    private Observable<Transaction> withTransactions(Observable<? extends Product> productObservable) {
        return productObservable
                .flatMap(p -> TransactionFixtures.randomTransactions(p.getCustomerId(), p, rn.nextInt(12) + 3))
                .flatMap(transaction -> transactionService.createTransaction(transaction))
                .last()
                ;
    }

}
