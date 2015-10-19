package com.senacor.reactile.service.appointment;

import com.senacor.reactile.hystrix.interception.HystrixCmd;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;

public class BranchServiceImpl implements BranchService {

    private final BranchDatabase branchDatabase;

    @Inject
    public BranchServiceImpl(BranchDatabase branchDatabase) {
        this.branchDatabase = branchDatabase;
    }

    @Override
    public void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler) {
        Rx.bridgeHandler(getBranch(branchId), resultHandler);
    }

    @HystrixCmd(BranchServiceImplGetBranchCommand.class)
    public Observable<Branch> getBranch(String branchId) {
        return Observable.create(subscriber -> {
            Branch branch = branchDatabase.findById(branchId);
            if (branch == null) {
                subscriber.onError(new NoSuchElementException(branchId));
            } else {
                subscriber.onNext(branch);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void findBranches(List<String> branchIds, Handler<AsyncResult<BranchList>> resultHandler) {
        Rx.bridgeHandler(findBranches(branchIds), resultHandler);
    }

    public Observable<BranchList> findBranches(final List<String> branchIds) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(BranchList.newBuilder().withBranches(branchDatabase.findByIds(branchIds)).build());
                subscriber.onCompleted();
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public void getAllBranches(Handler<AsyncResult<BranchList>> resultHandler) {
        Rx.bridgeHandler(getAllBranches(), resultHandler);
    }

    public Observable<BranchList> getAllBranches() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(BranchList.newBuilder().withBranches(branchDatabase.findAll()).build());
                subscriber.onCompleted();
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        });
    }

}
