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

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
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

    }

    @Test
    public void testGetAppointmentById() throws Exception {
        Appointment appointment = appointmentService.getAppointmentById("1").toBlocking().first();
        assertThat(appointment.getId(), Matchers.is("1"));
    }

    @Test
    public void testGetAppointmentsByCustomer() throws Exception {

    }

    @Test
    public void testGetAppointmentsByBranch() throws Exception {

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