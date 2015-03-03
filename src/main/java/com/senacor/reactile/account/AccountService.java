package com.senacor.reactile.account;

import com.senacor.reactile.customer.CustomerId;
import rx.Observable;

public interface AccountService {

    Observable<Account> getAccount(AccountId accountId);
    Observable<Account> getAccountsForCustomer(CustomerId customerId);
}
