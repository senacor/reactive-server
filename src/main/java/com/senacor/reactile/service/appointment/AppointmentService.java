package com.senacor.reactile.service.appointment;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.service.branch.Branch;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

public interface AppointmentService {

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAllAppointments();

    @Action(returnType = Appointment.class)
    public Observable<Appointment> getAppointmentById(String appointmentId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> getAppointmentsByBranchAndDate(String branchId, Long date);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByUser(String userId);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> getAppointmentsByUserAndDate(String userId, Long date);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> deleteAppointment(String appointmentId);
}
