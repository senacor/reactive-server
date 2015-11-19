package com.senacor.reactile.service.appointment;

import com.senacor.reactile.abstractservice.Action;
import rx.Observable;

public interface AppointmentService {

    String APPOINTMENT_EVENT_UPDATE_APPOINTMENT = "AppointmentService#updateAppointment";
    String APPOINTMENT_EVENT_DELETE_APPOINTMENT = "AppointmentService#deleteAppointment";

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAllAppointments();

    @Action(returnType = Appointment.class)
    public Observable<Appointment> getAppointmentById(String appointmentId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByBranchAndDate(String branchId, Long date);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByUser(String userId);

    @Action(returnType = AppointmentList.class)
    public Observable<AppointmentList> getAppointmentsByUserAndDate(String userId, Long date);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment);

    @Action(returnType = Appointment.class)
    public Observable<Appointment> deleteAppointment(String appointmentId);
}
