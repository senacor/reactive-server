package com.senacor.reactile.service.appointment;

import javax.inject.Inject;

import com.google.common.base.Verify;
import com.google.common.collect.Lists;

import rx.Observable;

/**
 * @author Andreas Karoly, Senacor Technologies AG
 */
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentDatabase appointmentDatabase;

    @Inject
    public AppointmentServiceImpl(AppointmentDatabase appointmentDatabase) {
        this.appointmentDatabase = appointmentDatabase;
    }

    @Override
    public Observable<AppointmentList> getAllAppointments() {
        return Observable.just(
                new AppointmentList(Lists.newArrayList(appointmentDatabase.findAll())));
    }

    @Override
    public Observable<Appointment> getAppointmentById(String appointmentId) {
        Verify.verifyNotNull(appointmentId, "appointmentId must be provided");
        return Observable.just(appointmentDatabase.findById(appointmentId));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        Verify.verifyNotNull(customerId, "customerId must be provided");

        return Observable.just(new AppointmentList(Lists.newArrayList(appointmentDatabase.
                findByCustomerId(customerId))));
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
