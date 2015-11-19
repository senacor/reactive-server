package com.senacor.reactile.service.branch;

public final class BranchFixtures {

	private BranchFixtures() {
	}

	public static Branch newBranch(String branchId, String name) {
		return new Branch(branchId, name, null);
	}

}
