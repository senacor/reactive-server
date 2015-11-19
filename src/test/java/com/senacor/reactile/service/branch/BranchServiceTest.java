package com.senacor.reactile.service.branch;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.json.JsonizableList;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerFixtures;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.customer.CustomerService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static org.junit.Assert.assertThat;

public class BranchServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private BranchService service;

    @Test
    public void thatBranchIsReturned() {
        Branch branch = service.getBranch("1").toBlocking().first();
        assertThat(branch.getId(), CoreMatchers.equalTo("1"));
    }

    @Test
    public void thatBranchesAreReturned() {
        JsonizableList<String> branchIds = new JsonizableList<>(Arrays.asList("1", "2"));

        BranchList branchList = service.findBranches(branchIds).toBlocking().first();

        List<String> returnedBranchIds = branchList.getBranches().stream().map(Branch::getId).collect(Collectors.toList());

        assertThat(returnedBranchIds, CoreMatchers.equalTo(Arrays.asList("1", "2")));
    }
}
