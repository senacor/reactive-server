package com.senacor.reactile.service.appointment;

import com.google.common.base.Throwables;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class AppointmentServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private AppointmentService appointmentService;

    @Test
    public void testGetAllAppointments() throws Exception {
        AppointmentList appointments = appointmentService.getAllAppointments().toBlocking().first();
        assertThat(appointments, is(notNullValue()));
        List<String> idList = appointments.getAppointmentList().stream().map(Appointment::getId).collect(Collectors.toList());
        assertThat(idList, Matchers.hasItems("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));
    }

    @Test
    public void testGetAppointmentById() throws Exception {
        Appointment appointment = appointmentService.getAppointmentById("1").toBlocking().first();
        assertThat(appointment.getId(), is("1"));
    }

    @Test
    public void testGetAppointmentsByCustomer() throws Exception {
        AppointmentList appointments = appointmentService.getAppointmentsByCustomer("cust-100000").toBlocking().first();
        assertNotNull(appointments);
        for (Appointment appointment : appointments.getAppointmentList()) {
            assertEquals(appointment.getCustomerId(), "cust-100000");
        }
    }

    @Test
    public void testGetAppointmentsByBranch() throws Exception {
        AppointmentList appointments = appointmentService.getAppointmentsByBranch("2").toBlocking().first();
        assertNotNull(appointments);
        for (Appointment appointment : appointments.getAppointmentList()) {
            assertEquals(appointment.getBranchId(), "2");
        }
    }

    @Test
    public void testGetAppointmentsByBranchAndDate() throws Exception {
//        AppointmentList appointments = appointmentService.getAppointmentsByBranchAndDate("2", 2015111916150000l).toBlocking().first();

    }

    @Test
    public void testGetAppointmentsByUser() throws Exception {
        AppointmentList appointments = appointmentService.getAppointmentsByUser("momann").toBlocking().first();
        assertThat(appointments, is(notNullValue()));
        for (Appointment appointment : appointments.getAppointmentList()) {
            assertThat(appointment.getUserId(), is("momann"));
        }
    }

    @Test
    public void testGetAppointmentsByUserAndDate() throws Exception {

    }

    @Test
    public void testCreateOrUpdateAppointment() throws Exception {
        appointmentService.createOrUpdateAppointment(Appointment.newBuilder().withId("999").build()).toBlocking().first();
        assertThat(appointmentService.getAppointmentById("999").toBlocking().first().getId(), is("999"));
    }

    @Test
    public void testReceiveAppointmentCreatedOrUpdatedEvt() throws Exception {
        final LinkedBlockingQueue<AppointmentCreatedOrUpdatedEvt> queue = new LinkedBlockingQueue<>();

        // listen to events
        vertxRule.eventBus().consumer(AppointmentService.ADDRESS_CREATE_OR_UPDATE_APPOINTMENT)
                .toObservable()
                .map(Message::body)
                .cast(JsonObject.class)
                .map(AppointmentCreatedOrUpdatedEvt::fromJson)
                .doOnError(throwable -> fail(throwable.getMessage() + Throwables.getStackTraceAsString(throwable)))
                .subscribe(queue::add);

        // send event ...
        testCreateOrUpdateAppointment();

        AppointmentCreatedOrUpdatedEvt event = queue.poll(2L, TimeUnit.SECONDS);
        logger.info("Received Event: " + event);
        assertNotNull("No Event Received", event);
        assertNotNull("event.id must not be null", event.getId());
        assertNotNull("event.appointment must not be null", event.getAppointment());
    }
    @Test
    public void testDeleteAppointment() throws Exception {
        Appointment deleted = appointmentService.deleteAppointment("11").toBlocking().first();
        assertThat(deleted.getId(), is("11"));
        Appointment appointment = appointmentService.getAppointmentById("11").toBlocking().first();
        assertThat(appointment.getId(), is(Matchers.nullValue()));
    }
}