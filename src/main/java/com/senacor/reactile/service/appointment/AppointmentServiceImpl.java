package com.senacor.reactile.service.appointment;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import rx.Observable;

/**
 * Created by sbode on 19.11.15.
 */
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Override
    public Observable<AppointmentList> getAllAppointments() {
        return null;
    }

    @Override
    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return null;
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        return null;
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        return null;
    }

    @Override
    public Observable<Appointment> getAppointmentsByBranchAndDate(String branchId, Long date) {
        return null;
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByUser(String userId) {
        return null;
    }

    @Override
    public Observable<Appointment> getAppointmentsByUserAndDate(String userId, Long date) {
        return null;
    }

    @Override
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return null;
    }

    @Override
    public Observable<Appointment> deleteAppointment(String appointmentId) {
        return null;
    }
}
