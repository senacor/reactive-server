package com.senacor.reactile;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxException;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.service.ServiceIndentifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DeploymentOptionsLoader {

    static DeploymentOptions load(String identifier) throws IOException {
        ServiceIndentifier serviceID = new ServiceIndentifier(VerticleFactory.removePrefix(identifier));
        String descriptorFile = serviceID.descriptorFilename();
        JsonObject descriptor;
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(descriptorFile)) {
            if (is == null) {
                throw new VertxException("Cannot find file " + descriptorFile + " on classpath");
            }
            try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
                String conf = scanner.next();
                descriptor = new JsonObject(conf);
            } catch (NoSuchElementException e) {
                throw new VertxException(descriptorFile + " is empty");
            } catch (DecodeException e) {
                throw new VertxException(descriptorFile + " contains invalid json");
            }
        }
        JsonObject depOptions = new JsonObject();
        JsonObject serviceOptions = descriptor.getJsonObject("options", new JsonObject());
        JsonObject serviceConfig = serviceOptions.getJsonObject("config", new JsonObject());
        depOptions.mergeIn(serviceOptions);
        depOptions.put("config", serviceConfig);
        return new DeploymentOptions(depOptions);
    }
}
