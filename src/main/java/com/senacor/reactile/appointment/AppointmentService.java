package com.senacor.reactile.appointment;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.time.ZonedDateTime;
import java.util.Collection;

@ProxyGen
@VertxGen
public interface AppointmentService {

    static final String ADDRESS = "AppointmentService";

    static final String ADDRESS_EVENT_GET_APPOINTMENT_BY_ID = AppointmentService.ADDRESS + "#getAppointmentById";

    void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByBranch(String branchId, Handler<AsyncResult<Collection<Appointment>>> resultHandler);

    void getAppointmentsByBranchAndDate(String branchId, ZonedDateTime date, Handler<AsyncResult<Collection<Appointment>>> resultHandler);

    void getAppointmentsByUser(String userId, Handler<AsyncResult<Collection<Appointment>>> resultHandler);

    void getAppointmentsByUserAndDate(String userId, ZonedDateTime date, Handler<AsyncResult<Collection<Appointment>>> resultHandler);

    void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler);

    void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);
}
