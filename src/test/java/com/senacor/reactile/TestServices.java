package com.senacor.reactile;

public enum TestServices implements ServiceIdProvider {

    EmbeddedMongo("com.senacor:reactile-embedded-mongo");

    private final String serviceName;

    TestServices(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getId() {
        return "service:" + serviceName;
    }


}
