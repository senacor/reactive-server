package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.service.branch.BranchId;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.UserId;

/**
 * Factory for StartCommand
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 16:10
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public interface StartCommandFactory {

    StartCommand create(UserId userId, CustomerId customerId, BranchId branchId);
}
