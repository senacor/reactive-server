package com.senacor.reactile.appointment;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        Appointment appointment = service.getAppointmentByIdObservable("2").toBlocking().single();

        assertEquals("Consulting 3", appointment.getName());
    }

    @Test
    public void thatAppointmentCanBeUpdated() throws Exception {
        Appointment appointment = service.getAppointmentByIdObservable("3").toBlocking().single();
        Appointment expected = appointment.newBuilder(appointment).withName("Unsulting 2").build();

        Appointment result = service.createOrUpdateAppointmentObservable(expected).toBlocking().first();

        System.out.println("expecting: " + expected.toJson());
        assertEquals(expected.toJson(), result.toJson());
    }

    @Test
    public void thatAppointmentCanBeDeleted() throws Exception {
        Appointment original = service.deleteAppointmentObservable("4").toBlocking().single();
        assertNotNull(original);
        try {
            service.getAppointmentByIdObservable("4").toBlocking().single();
        } catch (ReplyException re) {
            assertEquals("Appointment with ID 4 doesn't exist.", re.getMessage());
        }
    }

    @Test
    public void getAppointmentsByBranchTest() {
        final int expectedListSize = 5;
        AppointmentList appointmentList = service.getAppointmentsByBranchObservable("1").toBlocking().first();
        assertNotNull(appointmentList);

        assertEquals(expectedListSize, appointmentList.getAppointmentList().size());
    }

    @Test
    public void getAppointmentsByUserTest() {
        final int expectedListSize = 6;
        AppointmentList appointmentList = service.getAppointmentsByUserObservable("aangel").toBlocking().first();
        assertNotNull(appointmentList);

        assertEquals(expectedListSize, appointmentList.getAppointmentList().size());
    }

    @Test
    public void getAppoinmentByCustomerTest() {
        final int expectedListSize = 12;
        AppointmentList appointmentList = service.getAppointmentsByCustomerObservable("1").toBlocking().first();
        assertNotNull(appointmentList);

        appointmentList.getAppointmentList().forEach(appointment -> {
            System.out.println(appointment.toJson());
        });

        assertEquals(expectedListSize, appointmentList.getAppointmentList().size());
    }
}
