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

    void getAllAppointments(Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByCustomer(String customerId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByBranch(String branchId, String eventAddress, Handler<AsyncResult<String>> resultHandler);

    void getAppointmentsByBranchAndDate(String branchId, Long date, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByUserAndDate(String userId, Long date, Handler<AsyncResult<Appointment>> resultHandler);

    void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler);

    void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);
}
