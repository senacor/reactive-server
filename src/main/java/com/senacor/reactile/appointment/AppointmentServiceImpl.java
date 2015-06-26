package com.senacor.reactile.appointment;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import javax.inject.Inject;

public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentDatabase database;
    private final Vertx vertx;

    @Inject
    public AppointmentServiceImpl(AppointmentDatabase database, Vertx vertx) {
        this.database = database;
        this.vertx = vertx;
    }

    @Override
    public void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(getAppointmentById(appointmentId), resultHandler);
    }

    @Override
    public void getAppointmentsByBranch(String branchId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByBranch(branchId), resultHandler);
    }

    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        final AppointmentList appointmentList = new AppointmentList();

        if (branchId == null || branchId.isEmpty()) {
            return Observable.just(appointmentList);
        }

        database.findAll()
                .stream()
                .filter(appointment -> appointment.getBranchId().equals(branchId))
                .forEach(appointment -> appointmentList.addAppointment(appointment));
        return Observable.just(appointmentList);
    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, Handler<AsyncResult<Appointment>> resultHandler) {

    }

    @Override
    public void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByUser(userId), resultHandler);
    }

    public Observable<AppointmentList> getAppointmentsByUser(String userId) {
        final AppointmentList appointmentList = new AppointmentList();

        if (userId == null || userId.isEmpty()) {
            return Observable.just(appointmentList);
        }

        database.findAll()
                .stream()
                .filter(appointment -> appointment.getUserId().equals(userId))
                .forEach(appointment -> appointmentList.addAppointment(appointment));
        return Observable.just(appointmentList);
    }

    @Override
    public void getAppointmentsByUserAndDate(String userId, Handler<AsyncResult<Appointment>> resultHandler) {

    }

    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return Observable.just(database.findById(appointmentId));
    }


    @Override
    public void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(
                Observable.just(database.saveOrUpdate(appointment))
                , resultHandler);
    }

    @Override
    public void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(
                Observable.just(database.deleteById(appointmentId))
                , resultHandler);
    }

}
