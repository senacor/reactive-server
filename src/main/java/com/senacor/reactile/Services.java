package com.senacor.reactile;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum Services implements ServiceIdProvider {

    EmbeddedMongo("com.senacor.reactile.embedded-mongo"),
    NewsService("com.senacor.reactile.news-service"),
    UserService("com.senacor.reactile.user-service"),
    CustomerService("com.senacor.reactile.customer-service", EmbeddedMongo),
    AppointmentSerivce("com.senacor.reactile.appointment-service"),
    BranchService("com.senacor.reactile.branch-service"),
    AccountService("com.senacor.reactile.account-service", EmbeddedMongo),
    CreditCardService("com.senacor.reactile.creditcard-service", EmbeddedMongo),
    TransactionService("com.senacor.reactile.transaction-service", EmbeddedMongo),
    PushNotificationService("com.senacor.reactile.pushnotification-service"),
    HystrixMetricsStreamVerticle("com.senacor.reactile.hystrix-metrics-stream-service"),
    GatewayService("com.senacor.reactile.gateway-service", UserService, CustomerService, AccountService,
        CreditCardService, TransactionService, AppointmentSerivce, BranchService, PushNotificationService,
        HystrixMetricsStreamVerticle, NewsService);

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
