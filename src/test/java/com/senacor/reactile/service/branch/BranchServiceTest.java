package com.senacor.reactile.service.branch;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.json.JsonizableList;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BranchServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);


    @Inject
    private BranchService branchService;

    @Test
    public void shouldReturnBranchesForId() {
        Observable<Branch> branch = branchService.getBranch("1");

        Branch b = branch.toBlocking().first();
        assertEquals("1", b.getId());
    }

    @Test
    public void shouldReturnBranchesFordIds() {
        List<String> json = new ArrayList<>();
        json.add("1");
        json.add("5");
        JsonizableList<String> branchIds = new JsonizableList<>(json);
        Observable<BranchList> branchList = branchService.findBranches(branchIds);

        BranchList b = branchList.toBlocking().first();
        assertEquals(2, b.getBranches().size());
        assertEquals("1", b.getBranches().get(0).getId());
        assertEquals("5", b.getBranches().get(1).getId());
    }

    @Test
    public void shouldReturnAllBranches() {
        Observable<BranchList> branchList = branchService.getAllBranches();

        BranchList b = branchList.toBlocking().first();
        assertEquals(10, b.getBranches().size());
    }
}