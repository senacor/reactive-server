package com.senacor.reactile;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum Services implements ServiceIdProvider {

    EmbeddedMongo("com.senacor.innolab.reactile:embedded-mongo:1.0.0"),
    UserConnector("com.senacor.innolab.reactile:user-connector:1.0.0"),
    UserService("com.senacor.innolab.reactile:user-service:1.0.0", UserConnector),
    CustomerService("com.senacor.innolab.reactile:customer-service:1.0.0", EmbeddedMongo),
    AccountService("com.senacor.innolab.reactile:account-service:1.0.0", EmbeddedMongo),
    CreditCardService("com.senacor.innolab.reactile:creditcard-service:1.0.0", EmbeddedMongo),
    TransactionService("com.senacor.innolab.reactile:transaction-service:1.0.0", EmbeddedMongo),
    GatewayService("com.senacor.innolab.reactile:gateway-service:1.0.0", UserService, CustomerService, AccountService, CreditCardService, TransactionService);


    private final String serviceName;
    private final Set<ServiceIdProvider> dependencies;

    Services(String serviceName, ServiceIdProvider... services) {
        this.serviceName = serviceName;
        dependencies = ImmutableSet.copyOf(services);
    }

    @Override
    public String getId() {
        return "service:" + serviceName;
    }

    @Override
    public Set<ServiceIdProvider> dependsOn() {
        return dependencies;
    }

    @Override
    public int order() {
        return ordinal();
    }


}
