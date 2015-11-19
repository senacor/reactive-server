package com.senacor.reactile.service.appointment;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
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
        List<String> idList = appointments.getAppointmentList().stream().map(a -> a.getId()).collect(Collectors.toList());
        assertThat(idList, containsInAnyOrder("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));
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

    }

    @Test
    public void testGetAppointmentsByUser() throws Exception {

    }

    @Test
    public void testGetAppointmentsByUserAndDate() throws Exception {

    }

    @Test
    public void testCreateOrUpdateAppointment() throws Exception {

    }

    @Test
    public void testDeleteAppointment() throws Exception {

    }
}