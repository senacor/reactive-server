package com.senacor.reactile.service.branch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.json.JsonizableList;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
@RunWith(JUnitParamsRunner.class)
public class BranchServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private BranchService branchService;

    @Test
    @Parameters({"1, Bonn", "2, Munich", "3, Stuttgart", "4, Berlin", "5, Hamburg", "6, Nürnberg", "7, Frankfurt", "8, Leipzig", "9, Dresden",
        "10, Hof"})
    public void thatBranchServiceFindBranchFindsCorrectBranch(String branchId, String name) {
        final Branch branch = branchService.getBranch(branchId).toBlocking().first();

        assertThat(branch).isNotNull();
        assertThat(branch.getName()).isEqualTo(name);
    }

    @Test
    @Parameters({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
    public void thatBranchServiceFindBranchesFindsCorrectBranches(int amount) {
        final JsonizableList<String> ids = new JsonizableList<>(IntStream.range(0, amount) //
            .map(i -> i + 1) //
            .mapToObj(Integer::toString) //
            .collect(Collectors.toList()));

        final BranchList branches = branchService.findBranches(ids).toBlocking().first();

        final List<String> branchIds = branches.getBranches().stream().map(Branch::getId).collect(Collectors.toList());
        assertThat(branchIds).hasSameElementsAs(ids.toList());
    }

    @Test
    public void testThatBranchServiceGetAllBranchesReturnsAllBranches() {
        final BranchList branches = branchService.getAllBranches().toBlocking().first();

        final List<String> branchIds = branches.getBranches().stream().map(Branch::getId).collect(Collectors.toList());
        assertThat(branchIds).hasSameElementsAs(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
    }
}