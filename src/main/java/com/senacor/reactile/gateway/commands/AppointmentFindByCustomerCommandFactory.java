package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.service.customer.CustomerId;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
public interface AppointmentFindByCustomerCommandFactory {

    AppointmentFindByCustomerCommand create(CustomerId customerId);
}
