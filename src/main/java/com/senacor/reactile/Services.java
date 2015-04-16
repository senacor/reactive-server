package com.senacor.reactile;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum Services implements ServiceIdProvider {

    EmbeddedMongo("com.senacor.innolab.reactile.embedded-mongo"),
    UserConnector("com.senacor.innolab.reactile.user-connector"),
    UserService("com.senacor.innolab.reactile.user-service", UserConnector),
    CustomerService("com.senacor.innolab.reactile.customer-service", EmbeddedMongo),
    AccountService("com.senacor.innolab.reactile.account-service", EmbeddedMongo),
    CreditCardService("com.senacor.innolab.reactile.creditcard-service", EmbeddedMongo),
    TransactionService("com.senacor.innolab.reactile.transaction-service", EmbeddedMongo),
    PushNotificationService("com.senacor.innolab.reactile.pushnotification-service"),
    HystrixMetricsStreamVerticle("com.senacor.innolab.reactile.hystrix-metrics-stream-service"),
    GatewayService("com.senacor.innolab.reactile.gateway-service", UserService, CustomerService, AccountService, CreditCardService, TransactionService, PushNotificationService, HystrixMetricsStreamVerticle);


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
