package com.senacor.reactile.service.appointment;

import rx.Observable;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        Appointment appointment = database.findById(appointmentId);
        return Observable.just(appointment);
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        List<Appointment> appointments = database.findByCustomerId(customerId);
        return Observable.just(new AppointmentList(appointments));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        List<Appointment> appointments = database.findByBranchId(branchId);
        return Observable.just(new AppointmentList(appointments));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranchAndDate(String branchId, Long date) {
        List<Appointment> appointments = database.findByBranchId(branchId);
        List<Appointment> result = new ArrayList<>();
        Observable.from(appointments).filter(a -> matchesDate(a, date)).forEach(a -> result.add(a));
        return Observable.just(new AppointmentList(result));
    }

    private Boolean matchesDate(Appointment a, Long longDate) {
        LocalDate date = LocalDate.ofEpochDay(longDate);
        return a.getStart().toLocalDate().equals(date);
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
