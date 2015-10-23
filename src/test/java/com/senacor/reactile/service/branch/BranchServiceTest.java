package com.senacor.reactile.service.branch;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class BranchServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.service.branch.BranchService service;

    @Test
    public void testBranch() throws Exception {
        Branch bonn = service.getBranchObservable("1").toBlocking().first();
        assertThat(bonn, hasProperty("address", hasProperty("city", equalTo("Bonn"))));
    }

    @Test
    public void testFindBranches() throws Exception {
        BranchList branchList = service.findBranchesObservable(Arrays.asList("1", "2")).toBlocking().first();
        assertThat(branchList.getBranches(), hasSize(2));
    }

    @Test
    public void testAllBranches() throws Exception {
        BranchList branchList = service.getAllBranchesObservable().toBlocking().first();
        assertThat(branchList.getBranches(), hasSize(10));
    }

}