package com.senacor.reactile.service.branch;

import com.senacor.reactile.rx.Rx;
import com.senacor.reactile.service.customer.Address;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

public class BranchServiceImpl implements BranchService {

    private static final Logger logger = LoggerFactory.getLogger(BranchServiceImpl.class);

    @Inject
    BranchDatabase branchDatabase;

    @Inject
    Vertx vertx;

    public void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler) {
        Rx.bridgeHandler(getBranch(branchId), resultHandler);
    }

    private Observable<Branch> getBranch(String branchId) {
        return Observable.just(branchDatabase.findById(branchId));
    }


    public void findBranches(List<String> branchIds, Handler<AsyncResult<BranchList>> resultHandler) {
        Rx.bridgeHandler(getBranches(branchIds), resultHandler);
    }

    private Observable<BranchList> getBranches(List<String> branchIds) {
        return Observable.just(branchDatabase.findByIds(branchIds))
                .map(branches -> BranchList.newBuilder().withBranches(branches).build());
    }

    public void getAllBranches(Handler<AsyncResult<BranchList>> resultHandler) {
        Rx.bridgeHandler(getBranches(), resultHandler);
    }

    private Observable<BranchList> getBranches() {
        return Observable.just(branchDatabase.findAll())
                .map(branches -> BranchList.newBuilder().withBranches(branches).build());
    }

    @Override
    public void updateAddress(String branchId, Address address, Handler<AsyncResult<Branch>> resultHandler) {
        Rx.bridgeHandler(updateAddress(branchId, address), resultHandler);
    }

    public Observable<Branch> updateAddress(String branchId, Address address) {
        return Observable.just(branchDatabase.findById(branchId))
                .map(branch -> Branch.addOrReplaceAddress(branch, address))
                .map(branchDatabase::saveOrUpdate)
                .doOnNext(branch -> {
                    // 1. publish 'updateAddress' Event
                    String eventAddress = BranchService.ADDRESS_EVENT_UPDATE_ADDRESS;
                    logger.info("publishing on '" + eventAddress + "'...");
                    vertx.eventBus().publish(eventAddress, BranchAddressChangedEvt.newBuilder()
                            .withId(branchId)
                            .withNewAddress(address)
                            .build()
                            .toJson());
                    logger.info("publishing on '" + eventAddress + "' done");
                })
                .doOnError(throwable -> logger.error("updateAddress error", throwable));
    }

}
