package com.senacor.reactile.appointment;

import com.senacor.reactile.hystrix.interception.HystrixCmd;
import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentDatabase database;
    private final Vertx vertx;

    @Inject
    public AppointmentServiceImpl(AppointmentDatabase database, Vertx vertx) {
        this.database = database;
        this.vertx = vertx;
    }

    @Override
    public void getAllAppointments(Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(getAllAppointments(), resultHandler);
    }

    @HystrixCmd(AppointmentServiceImplGetAllAppointmentsCommand.class)
    public Observable<AppointmentList> getAllAppointments() {
        AppointmentList.Builder builder = new AppointmentList.Builder();

        database.findAll().forEach(appointment -> builder.getAppointmentList().add(appointment));
        return Observable.just(new AppointmentList(builder));
    }

    @Override
    public void getAppointmentById(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(getAppointmentById(appointmentId), resultHandler);
    }

    @Override
    public void getAppointmentsByCustomer(String customerId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByCustomer(customerId), resultHandler);
    }

    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return Observable.just(new AppointmentList());
            }
        AppointmentList.Builder builder = new AppointmentList.Builder();

        database.findAll()
                .stream()
                .filter(appointment -> appointment.getCustomerId().equals(customerId))
                .forEach(appointment -> builder.getAppointmentList().add(appointment));
        return Observable.just(new AppointmentList(builder));
    }

    @Override
    public void getAppointmentsByBranch(String branchId, String eventAddress, Handler<AsyncResult<String>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByBranch(branchId, eventAddress), resultHandler);
    }

    private Observable<String> getAppointmentsByBranch(String branchId, String eventAddress) {
        return Observable.create(subscriber -> {
            List<Appointment> appointmentList = database.findByBranchId(branchId);

            Observable
                    .from(appointmentList)
                    .subscribe(appointment -> {
                        logger.info("publishing on '" + eventAddress + "'...");
                        vertx.eventBus().publish(eventAddress, appointment
                                .toJson(), new DeliveryOptions().addHeader("type", "next"));
                        logger.info("publishing on '" + eventAddress + "' done");
                    });

            subscriber.onNext(eventAddress);

            vertx.eventBus().publish(eventAddress, null,
                    new DeliveryOptions().addHeader("type", "complete"));
        });
    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {

    }

    @Override
    public void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByUser(userId), resultHandler);
    }

    public Observable<AppointmentList> getAppointmentsByUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Observable.just(new AppointmentList());
        }

        AppointmentList.Builder builder = new AppointmentList.Builder();

        database.findAll()
                .stream()
                .filter(appointment -> appointment.getUserId().equals(userId))
                .forEach(appointment -> builder.getAppointmentList().add(appointment));
        return Observable.just(new AppointmentList(builder));
    }

    @Override
    public void getAppointmentsByUserAndDate(String userId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {

    }

    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return Observable.create(subscribe -> {
            Appointment appointment = database.findById(appointmentId);
            if (appointment != null) {
                subscribe.onNext(appointment);
            } else {
                subscribe.onError(new NullPointerException("Appointment with ID " + appointmentId + " doesn't exist."));
            }
        });
    }

    @Override
    public void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(
                createOrUpdateAppointment(appointment)
                , resultHandler);
    }

    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return Observable.just(database.saveOrUpdate(appointment));
    }

    @Override
    public void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(
                deleteAppointment(appointmentId)
                , resultHandler);
    }

    public Observable<Appointment> deleteAppointment(String appointmentId) {
        return Observable.just(database.deleteById(appointmentId));
    }
}
