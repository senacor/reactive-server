package com.senacor.reactile.gateway.commands;

/**
 * Factory for GetAppointmentCommand
 */
public interface GetAppointmentCommandFactory {

    GetAppointmentCommand create(String appointmentId);
}
