package com.senacor.reactile.service.branch;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;

public class BranchServiceTest {

	@ClassRule
	public final static VertxRule vertxRule = new VertxRule(Services.BranchService);

	@Rule
	public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

	@Rule
	public final MongoInitializer initializer = new MongoInitializer(vertxRule.vertx(), BranchServiceImpl.COLLECTION);

	@Inject
	private BranchService service;

	@Before
	public void init() {
		initializer.writeBlocking(BranchFixtures.newBranch("branch1", "Name branch1"),
				BranchFixtures.newBranch("branch2", "Name branch2"),
				BranchFixtures.newBranch("branch3", "Name branch3"),
				BranchFixtures.newBranch("branch4", "Name branch4"));

	}

	@Test
	public void thatBranchIsReturned_forBranchId() {
		Branch branch = service.getBranch(new BranchId("branch1")).toBlocking().first();
		assertThat(branch.getId(), equalTo("branch1"));
	}



}