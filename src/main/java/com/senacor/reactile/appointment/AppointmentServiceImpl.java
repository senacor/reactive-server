package com.senacor.reactile.appointment;

import com.senacor.reactile.rx.Rx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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
    public void getAppointmentsByBranch(String branchId, Handler<AsyncResult<AppointmentList>> resultHandler) {
        Rx.bridgeHandler(getAppointmentsByBranch(branchId), resultHandler);
    }

    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        if (branchId == null || branchId.isEmpty()) {
            return Observable.just(new AppointmentList());
        }

        AppointmentList.Builder builder = new AppointmentList.Builder();

        database.findAll()
                .stream()
                .filter(appointment -> appointment.getBranchId().equals(branchId))
                .forEach(appointment -> builder.getAppointmentList().add(appointment));
        return Observable.just(new AppointmentList(builder));
    }

    @Override
    public void getAppointmentsByBranchAndDate(String branchId, Handler<AsyncResult<Appointment>> resultHandler) {

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
    public void getAppointmentsByUserAndDate(String userId, Handler<AsyncResult<Appointment>> resultHandler) {

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
