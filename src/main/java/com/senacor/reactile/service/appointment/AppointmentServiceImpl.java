package com.senacor.reactile.service.appointment;

import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

public class AppointmentServiceImpl implements AppointmentService {


    final AppointmentDatabase database;

    @Inject
    public AppointmentServiceImpl(AppointmentDatabase database) {
        this.database = database;
    }

    @Override
    public Observable<AppointmentList> getAllAppointments() {
        Collection<Appointment> appointments = database.findAll();
        return Observable.just(new AppointmentList(new ArrayList<>(appointments)));
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
