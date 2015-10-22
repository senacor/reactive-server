package com.senacor.reactile.service.appointment;

import static rx.Observable.from;
import static rx.Observable.just;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.senacor.reactile.rx.Rx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

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
    public void getAllAppointments(Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(just(new AppointmentList(Lists.newArrayList(appointmentDatabase.findAll()))),
                resultHandler);
    }

    @Override
    public void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(just(appointmentDatabase.findById(appointmentId)), resultHandler);
    }

    @Override
    public void getAppointmentsByCustomer(String customerId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(just(new AppointmentList(appointmentDatabase.findByCustomerId(customerId))),
                resultHandler);
    }

    @Override
    public void getAppointmentsByBranch(String branchId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(just(new AppointmentList(appointmentDatabase.findByBranchId(branchId))),
                resultHandler);
    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(from(appointmentDatabase.findAll())
                .filter(appointment -> Objects.equals(branchId, appointment.getBranchId()))
                .filter(appointment -> keep(date, appointment)), resultHandler);
    }

    @Override
    public void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(from(appointmentDatabase.findAll())
                .filter(appointment -> Objects.equals(userId, appointment.getUserId()))
                        .collect(() -> new ArrayList<Appointment>(), ArrayList::add)
                .map(AppointmentList::new),
                resultHandler);
    }

    @Override
    public void getAppointmentsByUserAndDate(String userId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(from(appointmentDatabase.findAll())
                .filter(appointment -> Objects.equals(userId, appointment.getUserId()))
                .filter(appointment -> keep(date, appointment)), resultHandler);
    }

    @Override
    public void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(just(appointmentDatabase.saveOrUpdate(appointment)), resultHandler);
    }

    @Override
    public void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(just(appointmentDatabase.deleteById(appointmentId)), resultHandler);
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
