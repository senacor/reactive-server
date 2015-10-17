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

    @HystrixCmd(AppointmentServiceImplGetAppointmentsByCustomerCommand.class)
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return Observable.just(new AppointmentList());
        }
        AppointmentList res = AppointmentList.newBuilder()
                .withAppointments(database.findByCustomerId(customerId))
                .build();
        return Observable.just(res);
    }

    @Override
    public void getAppointmentsByBranch(String branchId, String eventAddress, Handler<AsyncResult<String>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByBranch(branchId, eventAddress), resultHandler);
    }

    private Observable<String> getAppointmentsByBranch(String branchId, String eventAddress) {
        return Observable.create(subscriber -> {
            database.findByBranchId(branchId).forEach(appointment -> {
                logger.info("publishing next on '" + eventAddress + "'...");
                vertx.eventBus().publish(eventAddress, appointment
                        .toJson(), new DeliveryOptions().addHeader("type", "next"));
                logger.info("publishing next on '" + eventAddress + "' done");
            });
            logger.info("publishing complete on '" + eventAddress + "'...");
            vertx.eventBus().publish(eventAddress, null,
                    new DeliveryOptions().addHeader("type", "complete"));
            logger.info("publishing complete on '" + eventAddress + "' done");

            subscriber.onNext(eventAddress);
            subscriber.onCompleted();
        });
    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {
        // TODO impl!
    }

    @Override
    public void getAppointmentsByUser(String userId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByUser(userId), resultHandler);
    }

    public Observable<AppointmentList> getAppointmentsByUser(final String userId) {
        if (userId == null || userId.isEmpty()) {
            return Observable.just(new AppointmentList());
        }

        final AppointmentList.Builder builder = new AppointmentList.Builder();

        database.findAll()
                .stream()
                .filter(appointment -> userId.equals(appointment.getUserId()))
                .forEach(appointment -> builder.getAppointmentList().add(appointment));
        return Observable.just(builder.build());
    }

    @Override
    public void getAppointmentsByUserAndDate(String userId, Long date, Handler<AsyncResult<Appointment>> resultHandler) {
        // TODO impl
    }

    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return Observable.create(subscribe -> {
            Appointment appointment = database.findById(appointmentId);
            if (appointment != null) {
                subscribe.onNext(appointment);
                subscribe.onCompleted();
            } else {
                subscribe.onError(new NullPointerException("Appointment with ID " + appointmentId + " doesn't exist."));
            }
        });
    }

    @Override
    public void createOrUpdateAppointment(Appointment appointment, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(createOrUpdateAppointment(appointment), resultHandler);
    }

    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return Observable.just(database.saveOrUpdate(appointment));
    }

    @Override
    public void deleteAppointment(String appointmentId, Handler<AsyncResult<Appointment>> resultHandler) {
        Rx.bridgeHandler(deleteAppointment(appointmentId), resultHandler);
    }

    public Observable<Appointment> deleteAppointment(String appointmentId) {
        return Observable.just(database.deleteById(appointmentId));
    }
}
