package com.senacor.reactile.appointment;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.observables.BlockingObservable;

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
        assertEquals("Bonn", branchObservable.toBlocking().first().getName());
    }

    @Test
    public void thatListIsComplete() throws Throwable {
        Observable<BranchList> branchObservable = service.getAllBranchesObservable();
        assertThat(branchObservable, is(not(nullValue())));
        BlockingObservable<BranchList> branchesBlockingObservable = branchObservable.toBlocking();
        BranchList branches = branchesBlockingObservable.first();
        assertEquals(10, branches.getBranches().size());
    }
}
