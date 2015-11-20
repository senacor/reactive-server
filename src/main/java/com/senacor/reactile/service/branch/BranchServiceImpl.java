package com.senacor.reactile.service.branch;

import java.util.List;

import com.google.inject.Inject;
import com.senacor.reactile.json.JsonizableList;

import io.vertx.rxjava.core.Vertx;
import rx.Observable;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class BranchServiceImpl implements BranchService {

    private final BranchDatabase branchDatabase;
    private final Vertx vertx;

    @Inject
    public BranchServiceImpl(BranchDatabase branchDatabase, Vertx vertx) {
        this.branchDatabase = branchDatabase;
        this.vertx = vertx;
    }

    @Override
    public Observable<Branch> getBranch(String branchId) {
        return Observable //
            .just(branchId) //
            .map(branchDatabase::findById);
    }

    @Override
    public Observable<BranchList> findBranches(JsonizableList<String> branchIds) {
        return Observable //
            .just(branchIds) //
            .map(JsonizableList::toList) //
            .map(branchDatabase::findByIds) //
            .map(this::listOfBranchesToBranchList);
    }

    @Override
    public Observable<BranchList> getAllBranches() {
        return Observable.<List<Branch>>create(subscriber -> {
            try {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(branchDatabase.findAll());
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).map(this::listOfBranchesToBranchList) //
            .doOnError(Throwable::printStackTrace);
    }

    private BranchList listOfBranchesToBranchList(List<Branch> branches) {
        return BranchList.newBuilder().withBranches(branches).build();
    }
}
