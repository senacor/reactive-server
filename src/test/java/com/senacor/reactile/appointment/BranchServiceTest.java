package com.senacor.reactile.appointment;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.observables.BlockingObservable;

import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class BranchServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.appointment.BranchService service;

    @Test
    public void thatBranchIsReturned() {
        Observable<Branch> branchObservable = service.getBranchObservable("0");
        assertThat(branchObservable, is(not(nullValue())));
        assertEquals("Bonn", branchObservable.toBlocking()
                                             .first()
                                             .getName());
    }

    @Test
    public void thatErrorIsPropagated() throws Throwable {
        // TODO: make sure this test works correctly
        Observable<Branch> branchObservable = service.getBranchObservable("abc");
        branchObservable.subscribe((b) -> {
            Assert.fail();
        }, (e) -> {
            Assert.assertEquals(NoSuchElementException.class, e.getClass());
        }, () -> {
            Assert.fail();
        });
    }

    @Test
    public void thatListIsComplete() throws Throwable {
        Observable<Branches> branchObservable = service.getAllBranchesObservable();
        assertThat(branchObservable, is(not(nullValue())));
        BlockingObservable<Branches> branchesBlockingObservable = branchObservable.toBlocking();
        Branches branches = branchesBlockingObservable.first();
        assertEquals(10, branches.getBranches()
                                 .size());
    }
}
