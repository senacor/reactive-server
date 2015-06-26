package com.senacor.reactile.appointment;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

public class AppointmentServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentSerivce);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.appointment.AppointmentService service;

    @Test
    public void thatAppointmentIsReturned() {
        Appointment appointment = service.getAppointmentByIdObservable("1").toBlocking().first();

        assertEquals("Consulting 2", appointment.getName());
    }

    @Test
    public void getAppointmentsByBranchTest() {
        final int expectedListSize = 6;
        AppointmentList appointmentList = service.getAppointmentsByBranchObservable("1").toBlocking().first();

        assertEquals(expectedListSize, appointmentList.getAppointmentList().size());
    }

    @Test
    public void getAppointmentsByUserTest() {
        final int expectedListSize = 6;
        AppointmentList appointmentList = service.getAppointmentsByUserObservable("aangel").toBlocking().first();

        appointmentList.getAppointmentList().forEach(appointment -> {
            System.out.println(appointment.toJson());
        });

        assertEquals(expectedListSize, appointmentList.getAppointmentList().size());
    }
}
