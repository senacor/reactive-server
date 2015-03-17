package com.senacor.reactile.bootstrap;

import com.senacor.reactile.codec.Codecs;
import com.senacor.reactile.gateway.InitialDataVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import java.util.LinkedHashSet;
import java.util.Set;

public class ApplicationStartup extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<String> deployedIds = new LinkedHashSet<>();

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        registerCodecs();
        startVerticle(MongoBootstrap.class.getName())
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        () -> {
                            logger.info(String.format("Deployed %s verticles with the following deploymentIds: %s", deployedIds.size(), deployedIds));
                            startServices(startFuture);

                        }
                );
    }

    private void startServices(Future<Void> startFuture) {
        services().flatMap(service -> startVerticle(service.getId()))
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        () -> {
                            logger.info(String.format("Deployed %s verticles with the following deploymentIds: %s", deployedIds.size(), deployedIds));
                            initializeData(startFuture);

                        }
                );
    }

    private void initializeData(Future<Void> startFuture) {
        startVerticle(InitialDataVerticle.class.getName())
                .subscribe(
                        deployedIds::add,
                        startFuture::fail,
                        () -> {
                            logger.info(String.format("Deployed %s verticles with the following deploymentIds: %s", deployedIds.size(), deployedIds));
                            startFuture.complete();
                        }
                );
    }

    private Observable<Services> services() {
        return Observable.from(Services.values());
    }

    private void registerCodecs() {
        Codecs.load(getVertx().eventBus());
    }

    private Observable<String> startVerticle(String identifier) {
        return vertx.deployVerticleObservable(identifier)
                .doOnNext(id -> logger.info("Starting verticle with identifier " + identifier + " and deploymentId " + id));
    }

    private Observable<Void> stopVerticle(String deploymentId) {
        logger.info("Stopping verticle with deploymentId " + deploymentId);
        return vertx.undeployObservable(deploymentId);
    }


    public static enum Services {
        UserConnector("com.senacor:reactile-user-connector:1.0.0"),
        UserService("com.senacor:reactile-user-service:1.0.0"),
        CustomerService("com.senacor:reactile-customer-service:1.0.0"),
        AccountService("com.senacor:reactile-account-service:1.0.0"),
        CreditCardService("com.senacor:reactile-creditcard-service:1.0.0"),
        TransactionService("com.senacor:reactile-transaction-service:1.0.0"),
        GatewayService("com.senacor:reactile-gateway-service:1.0.0");
        private final String identifier;

        Services(String identifier) {
            this.identifier = identifier;
        }

        public String getId() {
            return "service:" + identifier;
        }

    }
}
