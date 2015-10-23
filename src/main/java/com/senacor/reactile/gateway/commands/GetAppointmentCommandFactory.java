package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.UserId;

/**
 * Factory for GetAppointmentCommand
 */
public interface GetAppointmentCommandFactory {

    GetAppointmentCommand create(String appointmentId);
}
