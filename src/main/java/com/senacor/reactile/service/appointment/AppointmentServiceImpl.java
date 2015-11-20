package com.senacor.reactile.service.appointment;

import static com.google.common.collect.Lists.newArrayList;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
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
        return Observable.<Collection<Appointment>>create(subscriber -> {
            try {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(appointmentDatabase.findAll());
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).map(this::buildAppointmentList);
    }

    @Override
    public Observable<Appointment> getAppointmentById(String appointmentId) {
        return Observable //
            .just(appointmentId) //
            .map(appointmentDatabase::findById);
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByCustomer(String customerId) {
        return Observable //
            .just(customerId) //
            .map(appointmentDatabase::findByCustomerId) //
            .map(this::buildAppointmentList);
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranch(String branchId) {
        return Observable //
            .just(branchId) //
            .map(appointmentDatabase::findByBranchId) //
            .map(this::buildAppointmentList);
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByBranchAndDate(String branchId, Long date) {
        return filterByDate(getAppointmentsByBranch(branchId), date);
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByUser(String userId) {
        return findsAppointmentsFromObservableWithFilter( //
            getAllAppointments(), //
            appointment -> StringUtils.equals(userId, appointment.getUserId())) //
            .map(this::buildAppointmentList);
    }

    @Override
    public Observable<AppointmentList> getAppointmentsByUserAndDate(String userId, Long date) {
        return filterByDate(getAppointmentsByUser(userId), date);
    }

    @Override
    public Observable<Appointment> createOrUpdateAppointment(Appointment appointment) {
        return Observable //
            .just(appointment) //
            .<Appointment>map(appointmentDatabase::saveOrUpdate) //
            .doOnNext(savedAppointment -> {
                String eventAddress = ADDRESS_EVENT_UPDATE_APPOINTMENT;
                logger.info("creating or updating on '" + eventAddress + "'...");
                vertx.eventBus().publish( //
                    eventAddress, //
                    AppointmentCreatedOrUpdatedEvent //
                        .newBuilder() //
                        .withId(appointment.getId()) //
                        .withAppointment(appointment) //
                        .build() //
                        .toJson());
                logger.info("creating or updating on '" + eventAddress + "' done");
            }) //
            .doOnError(throwable -> logger.error("createOrUpdateAppointment error", throwable));
    }

    @Override
    public Observable<Appointment> deleteAppointment(String appointmentId) {
        return Observable //
            .just(appointmentId) //
            .map(appointmentDatabase::deleteById) //
            .doOnNext(appointment -> {
                String eventAddress = ADDRESS_EVENT_DELETE_APPOINTMENT;
                logger.info("deleting on '" + eventAddress + "'...");
                vertx.eventBus().publish( //
                    eventAddress, //
                    AppointmentDeletedEvt //
                        .newBuilder() //
                        .withId(appointmentId) //
                        .build() //
                        .toJson());
                logger.info("deleting on '" + eventAddress + "' done");
            }) //
            .doOnError(throwable -> logger.error("deleteAppointment error", throwable));
    }

    private Observable<AppointmentList> filterByDate(Observable<AppointmentList> observableAppointments, Long date) {
        final ZonedDateTime dateAsZonedDateTime = zonedDateTimeFromMillis(date);
        return findsAppointmentsFromObservableWithFilter( //
            observableAppointments, //
            appointment -> //
                !appointment.getStart().isAfter(dateAsZonedDateTime) //
                    && !appointment.getEnd().isBefore(dateAsZonedDateTime)) //
            .map(this::buildAppointmentList);
    }

    private Observable<List<Appointment>> findsAppointmentsFromObservableWithFilter(Observable<AppointmentList> observableAppointments,
        Predicate<Appointment> filter) {
        return observableAppointments.map( //
            appointmentList -> appointmentList.getAppointmentList() //
            .stream() //
            .filter(filter) //
            .collect(Collectors.toList()));
    }

    private AppointmentList buildAppointmentList(Collection<Appointment> appointments) {
        return AppointmentList.newBuilder().withAppointments(newArrayList(appointments)).build();
    }

    private ZonedDateTime zonedDateTimeFromMillis(Long date) {
        return new Date(date).toInstant().atZone(ZoneId.systemDefault());
    }
}
