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

<<<<<<< HEAD
    void getAppointmentsByBranch(String branchId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByBranchAndDate(String branchId, Handler<AsyncResult<Appointment>> resultHandler);

    void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler);

    void getAppointmentsByUserAndDate(String userId, Handler<AsyncResult<Appointment>> resultHandler);
=======
//    void getAppointmentsByBranch(String branchId, Handler<AsyncResult<Collection<Appointment>>> resultHandler);

//    void getAppointmentsByBranchAndDate(String branchId, ZonedDateTime date, Handler<AsyncResult<Collection<Appointment>>> resultHandler);

//    void getAppointmentsByUser(String userId, Handler<AsyncResult<Collection<Appointment>>> resultHandler);

//    void getAppointmentsByUserAndDate(String userId, ZonedDateTime date, Handler<AsyncResult<Collection<Appointment>>> resultHandler);
>>>>>>> 64cdc81dfba96a84d80a8de5ed28c834357eff34

    void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler);

    void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler);
}
