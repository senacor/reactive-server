package com.senacor.reactile.service.appointment;

import com.senacor.reactile.abstractservice.Action;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.UserId;
import rx.Observable;

public interface AppointmentService {

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAllAppointments();

    @Action(returnType = Appointment.class)
    public Observable<Appointment> getAppointmentById(AppointmentId appointmentId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByCustomer(CustomerId customerId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> getAppointmentsByBranchAndDate(String branchId, Long date);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByUser(UserId userId);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> getAppointmentsByUserAndDate(UserId userId, Long date);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> deleteAppointment(AppointmentId appointmentId);
}
