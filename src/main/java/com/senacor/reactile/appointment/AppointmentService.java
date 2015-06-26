package com.senacor.reactile.appointment;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface AppointmentService {

    static final String ADDRESS = "AppointmentService";

    static final String ADDRESS_EVENT_GET_APPOINTMENT_BY_ID = AppointmentService.ADDRESS + "#getAppointmentById";

    void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByCustomer(String customerId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByBranch(String branchId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByBranchAndDate(String branchId, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByUserAndDate(String userId, Handler<AsyncResult<Appointment>> resultHandler);

    void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler);

    void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);
}
