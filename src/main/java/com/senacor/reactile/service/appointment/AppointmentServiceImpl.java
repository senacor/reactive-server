package com.senacor.reactile.service.appointment;

import com.senacor.reactile.json.JsonizableList;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.service.user.UserId;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;
import rx.functions.Func1;

import javax.inject.Inject;
import java.util.List;

import static com.senacor.reactile.json.JsonObjects.marshal;
import static java.util.stream.Collectors.toList;

/**
 * Created with IntelliJ IDEA.
 * User: 27698019
 * Date: 19.11.15
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */
public class AppointmentServiceImpl implements AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    public static final String COLLECTION = "appointments";
    private final MongoService mongoService;
    private final Vertx vertx;

    @Inject
    public AppointmentServiceImpl(MongoService mongoService, Vertx vertx) {
        this.mongoService = mongoService;
        this.vertx = vertx;
    }

    @Override
    public Observable<AppointmentList> getAllAppointments() {
        return mongoService.findObservable(COLLECTION, null).map(toAppointmentList());
    }

    @Override
    public Observable<Appointment> getAppointmentById(AppointmentId appointmentId) {
        return mongoService.findOneObservable(COLLECTION, appointmentId.toJson(), null).map(Appointment::fromJson);
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(CustomerId customerId) {
        JsonObject query = new JsonObject().put("customerId", customerId.getId());
        return mongoService.findObservable(COLLECTION, query)
                .map(toAppointmentList());
    }

    private Func1<List<JsonObject>, AppointmentList> toAppointmentList() {
        return list -> {
            List<Appointment> transactions = list.stream().map(Appointment::fromJson).collect(toList());
            return new AppointmentList(transactions);
        };
    }


    @Override
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        JsonObject query = new JsonObject().put("branchId", branchId);
        return mongoService.findObservable(COLLECTION, query)
                .map(toAppointmentList());
    }

    @Override
    public Observable<Appointment> getAppointmentsByBranchAndDate(String branchId, Long date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByUser(UserId userId) {
        JsonObject query = new JsonObject().put("userId", userId.getId());
        return mongoService.findObservable(COLLECTION, query)
                .map(toAppointmentList());
    }

    @Override
    public Observable<Appointment> getAppointmentsByUserAndDate(UserId userId, Long date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return getAppointmentById(appointment.getId()).flatMap(tentativeAppointment ->
                {
                    if (tentativeAppointment != null) {
                        JsonObject query = new JsonObject().put("_id", appointment.getId().toValue());
                        return mongoService.updateObservable(COLLECTION, query, appointment.toJson())
                                .flatMap(res -> Observable.just(appointment));
                    } else {
                        JsonObject jasonDoc = appointment.toJson().put("_id", appointment.getId().toValue());
                        return mongoService.insertObservable(COLLECTION, jasonDoc)
                                .flatMap(id -> Observable.just(appointment));
                    }
                }).doOnError(throwable -> logger.error("createOrUpdate Appointment error", throwable));

    }

    @Override
    public Observable<Appointment> deleteAppointment(AppointmentId appointmentId) {
        Observable<Appointment> deletedObservable = this.getAppointmentById(appointmentId);
        JsonObject query = new JsonObject().put("_id", appointmentId.getId());
        mongoService.removeObservable(COLLECTION,query);
        return deletedObservable;
    }
}
