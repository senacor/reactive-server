package com.senacor.reactile.appointment;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

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

    @Test(expected = NoSuchElementException.class)
    public void thatErrorIsPropagated() throws Throwable {
        Observable<Branch> branchObservable = service.getBranchObservable("abc");
        branchObservable.toBlocking().first();
    }
}
