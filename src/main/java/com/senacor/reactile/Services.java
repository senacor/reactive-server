package com.senacor.reactile;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum Services implements ServiceIdProvider {

    UserConnector("com.senacor:reactile-user-connector:1.0.0"),
    UserService("com.senacor:reactile-user-service:1.0.0", UserConnector),
    CustomerService("com.senacor:reactile-customer-service:1.0.0"),
    AccountService("com.senacor:reactile-account-service:1.0.0"),
    CreditCardService("com.senacor:reactile-creditcard-service:1.0.0"),
    TransactionService("com.senacor:reactile-transaction-service:1.0.0");

    private final String serviceName;
    private final Set<Services> dependencies;

    Services(String serviceName, Services... services) {
        this.serviceName = serviceName;
        dependencies = ImmutableSet.copyOf(services);

    }

    @Override
    public String getId() {
        return "service:" + serviceName;
    }

    @Override
    public Set<? extends ServiceIdProvider> dependsOn() {
        return dependencies;
    }


}
