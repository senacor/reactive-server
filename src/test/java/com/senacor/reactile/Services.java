package com.senacor.reactile;

public enum Services implements ServiceIdProvider {

    UserConnector("com.senacor:reactile-user-connector:1.0.0"),
    UserService("com.senacor:reactile-user-service:1.0.0"),
    CustomerService("com.senacor:reactile-customer-service:1.0.0"),
    AccountService("com.senacor:reactile-account-service:1.0.0"),
    CreditCardService("com.senacor:reactile-creditcard-service:1.0.0"),
    TransactionService("com.senacor:reactile-transaction-service:1.0.0"),
    MongoConnector("io.vertx:vertx-mongo-embedded-db");

    private final String serviceName;

    Services(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getId() {
        return "service:" + serviceName;
    }


}
