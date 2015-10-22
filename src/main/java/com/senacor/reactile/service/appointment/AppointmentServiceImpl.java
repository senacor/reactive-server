package com.senacor.reactile.service.appointment;

import com.google.common.collect.Lists;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

import javax.inject.Inject;

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
        Rx.bridgeHandler(Observable.just(new AppointmentList(Lists.newArrayList(appointmentDatabase.findAll()))),
                resultHandler);
    }

    @Override
    public void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(Observable.just(appointmentDatabase.findById(appointmentId)), resultHandler);
    }

    @Override
    public void getAppointmentsByCustomer(String customerId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(Observable.just(new AppointmentList(appointmentDatabase.findByCustomerId(customerId))),
                resultHandler);
    }

    @Override
    public void getAppointmentsByBranch(String branchId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(Observable.just(new AppointmentList(appointmentDatabase.findByBranchId(branchId))),
                resultHandler);
    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {

    }

    @Override
    public void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler) {

    }

    @Override
    public void getAppointmentsByUserAndDate(String userId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {

    }

    @Override
    public void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler) {

    }

    @Override
    public void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(Observable.just(appointmentDatabase.deleteById(appointmentId)), resultHandler);
    }
}
