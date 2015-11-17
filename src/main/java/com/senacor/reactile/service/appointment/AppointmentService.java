package com.senacor.reactile.service.appointment;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface AppointmentService {

    String ADDRESS = "AppointmentService";

    void getAllAppointments(Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByCustomer(String customerId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByBranch(String branchId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByBranchAndDate(String branchId, Long date, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByUserAndDate(String userId, Long date, Handler<AsyncResult<Appointment>> resultHandler);

    void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler);

    void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);
}
