package com.senacor.reactile.service.branch;

import javax.inject.Inject;

import com.senacor.reactile.abstractservice.AbstractServiceVerticle;
import com.senacor.reactile.guice.Impl;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public class BranchServiceVerticle extends AbstractServiceVerticle {
    @Inject
    public BranchServiceVerticle(@Impl BranchService branchService) {
        super(branchService);
    }
}
