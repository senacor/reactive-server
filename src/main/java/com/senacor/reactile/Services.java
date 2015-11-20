package com.senacor.reactile;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum Services implements ServiceIdProvider {

    EmbeddedMongo("com.senacor.reactile.embedded-mongo"),
    AppointmentService("com.senacor.reactile.appointment-service"),
    UserService("com.senacor.reactile.user-service", EmbeddedMongo),
    CustomerService("com.senacor.reactile.customer-service", EmbeddedMongo),
    AccountService("com.senacor.reactile.account-service", EmbeddedMongo),
    BranchService("com.senacor.reactile.branch-service"),
    NewsService("com.senacor.reactile.news-service"),
    CreditCardService("com.senacor.reactile.creditcard-service", EmbeddedMongo),
    TransactionService("com.senacor.reactile.transaction-service", EmbeddedMongo),
    PushNotificationService("com.senacor.reactile.pushnotification-service"),
    HystrixMetricsStreamVerticle("com.senacor.reactile.hystrix-metrics-stream-service"),
    GatewayService("com.senacor.reactile.gateway-service", UserService, CustomerService, AccountService, BranchService,
        CreditCardService, TransactionService, PushNotificationService,
        HystrixMetricsStreamVerticle);

    private final String serviceName;
    private final Set<ServiceIdProvider> dependencies;

    Services(String serviceName, ServiceIdProvider... dependsOn) {
        this.serviceName = serviceName;
        dependencies = ImmutableSet.copyOf(dependsOn);
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
