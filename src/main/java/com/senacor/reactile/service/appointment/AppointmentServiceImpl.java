package com.senacor.reactile.service.appointment;

import com.senacor.reactile.service.customer.CustomerAddressChangedEvt;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sbode on 19.11.15.
 */
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentDatabase appointmentDatabase;
    private final Vertx vertx;

    @Inject
    public AppointmentServiceImpl(AppointmentDatabase appointmentDatabase, Vertx vertx) {
        this.appointmentDatabase = appointmentDatabase;
        this.vertx = vertx;
    }

    @Override
    public Observable<AppointmentList> getAllAppointments() {
        return Observable.defer(() -> Observable.just(AppointmentList.newBuilder().withAppointments(
                        new ArrayList<>(appointmentDatabase.findAll())).build()));
    }

    @Override
    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return Observable.defer(() -> Observable.just(appointmentDatabase.findById(appointmentId)));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        return Observable.defer(() -> Observable.just(
                AppointmentList.newBuilder().withAppointments(appointmentDatabase.findByCustomerId(customerId)).build()));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        return Observable.defer(() -> Observable.just(
                AppointmentList.newBuilder().withAppointments(appointmentDatabase.findByBranchId(branchId)).build()));
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranchAndDate(String branchId, Long date) {
        return null;
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByUser(String userId) {
       return Observable.defer(() -> Observable.just(
               AppointmentList.newBuilder().withAppointments(
                       appointmentDatabase.findAll()
                               .stream()
                               .filter(appointment -> userId.equals(appointment.getUserId()))
                               .collect(Collectors.toList()))
                       .build()));
    }

    @Override
    public Observable<Appointment> getAppointmentsByUserAndDate(String userId, Long date) {
        return null;
    }

    @Override
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return Observable.defer(() -> Observable.just(appointmentDatabase.saveOrUpdate(appointment)))
                .doOnNext(a -> {
                    String eventAddress = AppointmentService.EVENT_CREATE_OR_UPDATE_APPOINTMENT;
                    logger.info("publishing on '" + eventAddress + "'...");
                    vertx.eventBus().publish(eventAddress, AppointmentCreatedOrUpdatedEvt.newBuilder()
                            .withId(a.getId())
                            .withAppointment(a)
                            .build()
                            .toJson());
                    logger.info("publishing on '" + eventAddress + "' done");
                });
    }

    @Override
    public Observable<Appointment> deleteAppointment(String appointmentId) {
        return Observable.defer(
                () -> Observable.just(appointmentDatabase.deleteById(appointmentId)));
    }
}
