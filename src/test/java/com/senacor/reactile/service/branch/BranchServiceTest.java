package com.senacor.reactile.service.branch;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.json.JsonizableList;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by hannes on 19/11/15.
 */
public class BranchServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private BranchService service;


    @Test
    public void testThatBranchCanBeRead() throws Exception {

        final Branch branch = service.getBranch("2").toBlocking().first();
        assertEquals("Munich", branch.getName());
    }

    @Test
    public void testThatAllBranchesCanBeRead() throws Exception {

        final BranchList branchList = service.getAllBranches().toBlocking().first();
        assertEquals(10, branchList.getBranches().size());
    }

    @Test
    public void testThatBranchesForIdCanBeRead() throws Exception {

        final BranchList branchList = service.findBranches(new JsonizableList<>(Arrays.asList("1", "2", "3"))).toBlocking().first();
        assertEquals(3, branchList.getBranches().size());

    }
}
