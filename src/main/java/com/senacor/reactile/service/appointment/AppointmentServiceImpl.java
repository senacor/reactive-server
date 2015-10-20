package com.senacor.reactile.service.appointment;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;

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
    public void getAllAppointments(Handler<AsyncResult<AppointmentList>> resultHandler) {

    }

    @Override
    public void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
    }

    @Override
    public void getAppointmentsByCustomer(String customerId, Handler<AsyncResult<AppointmentList>> resultHandler) {
    }

    @Override
    public void getAppointmentsByBranch(String branchId, String eventAddress, Handler<AsyncResult<String>> resultHandler) {
    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {
    }

    @Override
    public void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler) {
    }

    public void getAppointmentsByUserAndDate(String userId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {
    }

    @Override
    public void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler) {
    }

    @Override
    public void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
    }

}
