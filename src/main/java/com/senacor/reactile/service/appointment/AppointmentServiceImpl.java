package com.senacor.reactile.service.appointment;

import com.google.inject.Inject;
import com.senacor.reactile.service.branch.Branch;
import io.vertx.core.Vertx;
import javafx.scene.input.ZoomEvent;
import rx.Observable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    @Inject
    private AppointmentDatabase appointmentDatabase;

    private final Vertx vertx;

    @Inject
    public AppointmentServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Observable<AppointmentList> getAllAppointments() {
        return null;
    }

    @Override
    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return Observable.<Appointment>create(subscriber -> {
            try {
                subscriber.onNext(appointmentDatabase.findById(appointmentId));
                subscriber.onCompleted();
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        return null;
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        return null;
    }

    @Override
    public Observable<Appointment> getAppointmentsByBranchAndDate(String branchId, Long date) {
        return Observable.<Appointment>create(subscriber -> {
            try {
                List<Appointment> byBranchId = appointmentDatabase.findByBranchId(branchId);
                byBranchId.stream().parallel().filter(a -> {
                    ZonedDateTime start = a.getStart();
                    ZonedDateTime end = a.getEnd();
                    ZonedDateTime current = new Date(date).toInstant().atZone(ZoneId.systemDefault());
                    return start.isBefore(current) && end.isAfter(current);
                }).forEach(a -> subscriber.onNext(a));
                subscriber.onCompleted();
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByUser(String userId) {
        return null;
    }

    @Override
    public Observable<Appointment> getAppointmentsByUserAndDate(String userId, Long date) {
        return null;
    }

    @Override
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return null;
    }

    @Override
    public Observable<Appointment> deleteAppointment(String appointmentId) {
        return null;
    }
}
