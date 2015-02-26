package com.senacor.reactile;

public enum Services implements ServiceIdProvider {

    UserConnector("com.senacor:reactile-user-connector:1.0.0"),
    UserService("com.senacor:reactile-user-service:1.0.0");

    private final String serviceName;

    Services(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getId() {
        return "service:" + serviceName;
    }


}
