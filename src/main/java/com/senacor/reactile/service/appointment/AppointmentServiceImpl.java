package com.senacor.reactile.service.appointment;

import com.google.common.collect.Lists;
import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Objects;

import static rx.Observable.from;
import static rx.Observable.just;

/**
 * @author Mihael Gorupec, Senacor Technologies AG
 */
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentDatabase appointmentDatabase;

    @Inject
    public AppointmentServiceImpl(AppointmentDatabase appointmentDatabase){
        this.appointmentDatabase = appointmentDatabase;
    }

    @Override
    public Observable<AppointmentList> getAllAppointments() {
        return just(new AppointmentList(Lists.newArrayList(appointmentDatabase.findAll())));
    }

    @Override
    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return just(appointmentDatabase.findById(appointmentId));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        return just(new AppointmentList(appointmentDatabase.findByCustomerId(customerId)));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        return just(new AppointmentList(appointmentDatabase.findByBranchId(branchId)));
    }

    @Override
    public Observable<Appointment> getAppointmentsByBranchAndDate(String branchId, Long date) {
        return from(appointmentDatabase.findAll())
                .filter(appointment -> Objects.equals(branchId, appointment.getBranchId()))
                .filter(appointment -> keep(date, appointment));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByUser(String userId) {
        return from(appointmentDatabase.findAll())
                .filter(appointment -> Objects.equals(userId, appointment.getUserId()))
                        .collect(() -> new ArrayList<Appointment>(), ArrayList::add)
                .map(AppointmentList::new);
    }

    @Override
    public Observable<Appointment> getAppointmentsByUserAndDate(String userId, Long date) {
        return from(appointmentDatabase.findAll())
                .filter(appointment -> Objects.equals(userId, appointment.getUserId()))
                .filter(appointment -> keep(date, appointment));
    }

    @Override
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return just(appointmentDatabase.saveOrUpdate(appointment));
    }

    @Override
    public Observable<Appointment> deleteAppointment(String appointmentId) {
        return(just(appointmentDatabase.deleteById(appointmentId)));
    }

    private boolean keep(Long date, Appointment appointment) {
        if(date == null) {
            return true;
        }

        if(appointment.getStart() == null || appointment.getEnd() == null) {
            return true;
        }

        long start = appointment.getStart().toEpochSecond();
        long end = appointment.getEnd().toEpochSecond();


        if(date >= start && date <= end) {
            return true;

        }
        return false;
    }
}
