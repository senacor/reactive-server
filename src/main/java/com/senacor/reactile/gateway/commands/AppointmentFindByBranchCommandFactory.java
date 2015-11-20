package com.senacor.reactile.gateway.commands;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public interface AppointmentFindByBranchCommandFactory {

    AppointmentFindByBranchCommand create(String branchId);
}
