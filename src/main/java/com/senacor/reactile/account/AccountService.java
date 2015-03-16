package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;

import java.util.List;

public interface AccountService {

    Observable<Account> getAccount(AccountId accountId);
    Observable<List<Account>> getAccountsForCustomer(CustomerId customerId);

    Observable<Account> createAccount(Account account);
}
