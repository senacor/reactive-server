package com.senacor.reactile.appointment;

import java.util.NoSuchElementException;
import javax.inject.Inject;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.Vertx;
import java.util.List;
import rx.Observable;

public class BranchServiceImpl implements BranchService {

  private final BranchDatabase branchDatabase;
  private final Vertx vertx;

  @Inject
  public BranchServiceImpl(BranchDatabase branchDatabase, Vertx vertx) {
    this.branchDatabase = branchDatabase;
    this.vertx = vertx;
  }

  @Override
  public void getBranch(String branchId, Handler<AsyncResult<Branch>> resultHandler) {
    Rx.bridgeHandler(getBranch(branchId), resultHandler);
  }

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

  public Observable<Branches> getAllBranches() {
    return Observable.create(subscriber -> {
      subscriber.onNext(new Branches(branchDatabase.findAll()));
        subscriber.onCompleted();
    });
  }

  @Override
  public void getAllBranches(Handler<AsyncResult<Branches>> resultHandler) {
    Rx.bridgeHandler(getAllBranches(), resultHandler);
  }

}
