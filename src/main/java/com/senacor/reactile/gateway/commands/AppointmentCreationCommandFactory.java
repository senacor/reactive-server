package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.service.appointment.Appointment;

/**
 * @author Andreas Karoly, Senacor Technologies AG
 */
public interface AppointmentCreationCommandFactory {
    AppointmentCreationCommand create(Appointment appointment);
}
