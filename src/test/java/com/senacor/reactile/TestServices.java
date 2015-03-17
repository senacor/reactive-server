package com.senacor.reactile;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum TestServices implements ServiceIdProvider {

    EmbeddedMongo("com.senacor:reactile-embedded-mongo"),
    UserConnector("com.senacor:reactile-user-connector:1.0.0"),
    UserService("com.senacor:reactile-user-service:1.0.0", UserConnector),
    CustomerService("com.senacor:reactile-customer-service:1.0.0", EmbeddedMongo),
    AccountService("com.senacor:reactile-account-service:1.0.0", EmbeddedMongo),
    CreditCardService("com.senacor:reactile-creditcard-service:1.0.0", EmbeddedMongo),
    TransactionService("com.senacor:reactile-transaction-service:1.0.0", EmbeddedMongo),
    GatewayService("com.senacor:reactile-gateway-service:1.0.0", UserService, CustomerService, AccountService, CreditCardService, TransactionService);


    private final String serviceName;
    private final Set<ServiceIdProvider> dependencies;

    TestServices(String serviceName, ServiceIdProvider... services) {
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
