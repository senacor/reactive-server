package com.senacor.reactile;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum Services implements ServiceIdProvider {

    EmbeddedMongo("com.senacor.innolab.reactile.embedded-mongo"),
    NewsService("com.senacor.innolab.reactile.news-service"),
    UserConnector("com.senacor.innolab.reactile.user-connector"),
    UserService("com.senacor.innolab.reactile.user-service", UserConnector),
    CustomerService("com.senacor.innolab.reactile.customer-service", EmbeddedMongo),
    AppointmentSerivce("com.senacor.innolab.reactile.appointment-service"),
    BranchService("com.senacor.innolab.reactile.branch-service"),
    AccountService("com.senacor.innolab.reactile.account-service", EmbeddedMongo),
    CreditCardService("com.senacor.innolab.reactile.creditcard-service", EmbeddedMongo),
    TransactionService("com.senacor.innolab.reactile.transaction-service", EmbeddedMongo),
    PushNotificationService("com.senacor.innolab.reactile.pushnotification-service"),
    HystrixMetricsStreamVerticle("com.senacor.innolab.reactile.hystrix-metrics-stream-service"),
    GatewayService("com.senacor.innolab.reactile.gateway-service", UserService, CustomerService, AccountService,
        CreditCardService, TransactionService, AppointmentSerivce, BranchService, PushNotificationService,
        HystrixMetricsStreamVerticle, NewsService);

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
