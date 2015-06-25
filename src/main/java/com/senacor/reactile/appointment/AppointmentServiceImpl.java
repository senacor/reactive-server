package com.senacor.reactile.appointment;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Collection;

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
    public void getAppointmentsByBranch(String branchId, Handler<AsyncResult<Collection<Appointment>>> resultHandler) {

    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, ZonedDateTime date, Handler<AsyncResult<Collection<Appointment>>> resultHandler) {

    }

    @Override
    public void getAppointmentsByUser(String userId, Handler<AsyncResult<Collection<Appointment>>> resultHandler) {

    }

    @Override
    public void getAppointmentsByUserAndDate(String userId, ZonedDateTime date, Handler<AsyncResult<Collection<Appointment>>> resultHandler) {

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
