package com.senacor.reactile.service.branch;


import com.google.inject.Inject;
import com.senacor.reactile.json.JsonizableList;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import rx.Observable;

public class BranchServiceImpl implements BranchService {

    @Inject
    private BranchDatabase branchDatabase;

    private final Vertx vertx;

    @Inject
    public BranchServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<Branch> getBranch(String branchId) {
        return Observable.<Branch>create(subscriber -> {
            try {
                subscriber.onNext(branchDatabase.findById(branchId));
                subscriber.onCompleted();
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<BranchList> findBranches(JsonizableList<String> branchIds) {
        return Observable.<BranchList>create(subscriber -> {
            try {
                BranchList.Builder builder = BranchList.newBuilder().withBranches(branchDatabase.findByIds(branchIds.toList()));
                subscriber.onNext(builder.build());
                subscriber.onCompleted();
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        });

    }

    @Override
    public Observable<BranchList> getAllBranches() {
        return Observable.<BranchList>create(subscriber -> {
            try {
                BranchList.Builder builder = BranchList.newBuilder().withBranches(branchDatabase.findAll());
                subscriber.onNext(builder.build());
                subscriber.onCompleted();
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        });
    }
}
