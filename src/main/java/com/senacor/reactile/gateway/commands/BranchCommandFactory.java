/*
 * Project       MKP
 * Copyright (c) 2009,2010,2011 DP IT Services GmbH
 *
 * All rights reserved.
 *
 * $Rev: $ 
 * $Date: $ 
 * $Author: $ 
 */
package com.senacor.reactile.gateway.commands;

/**
 * @author ccharles
 * @version $LastChangedVersion$
 */
public interface BranchCommandFactory {

  BranchOverviewCommand overview(String branchId);
}
