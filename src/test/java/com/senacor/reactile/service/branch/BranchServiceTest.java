package com.senacor.reactile.service.branch;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.json.JsonizableList;
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
    private BranchService service;

    @Test
    public void testBranch() throws Exception {
        Branch bonn = service.getBranch("1").toBlocking().first();
        assertThat(bonn, hasProperty("address", hasProperty("city", equalTo("Bonn"))));
    }

    @Test
    public void testFindBranches() throws Exception {
        BranchList branchList = service.findBranches(new JsonizableList<String>(Arrays.asList("1", "2"))).toBlocking().first();
        assertThat(branchList.getBranches(), hasSize(2));
    }

    @Test
    public void testAllBranches() throws Exception {
        BranchList branchList = service.getAllBranches().toBlocking().first();
        assertThat(branchList.getBranches(), hasSize(10));
    }

}