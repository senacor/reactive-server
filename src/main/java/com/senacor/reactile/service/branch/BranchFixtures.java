package com.senacor.reactile.service.branch;

import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.Country;

public final class BranchFixtures {

	private BranchFixtures() {
	}

	public static Branch newBranch(String branchId, String name) {
		return new Branch(branchId, name, new Address("coHint","street","zipCode","number","city",new Country("Germany", "DE"),1));
		
	}

}
