package com.senacor.reactile.service.appointment;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AppointmentServiceTest {

    @Inject
    AppointmentService appointmentService;


    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Test
    public void testDI() throws Exception {
        assertNotNull("You fucked it up with DI",appointmentService);
    }

    @Test
    public void thatAllAppointmentsAreReturned() throws Exception {
        Observable<AppointmentList> allAppointments = appointmentService.getAllAppointments();

        AppointmentList appointmentList = allAppointments.asObservable().toBlocking().first();
        assertNotNull(appointmentList);
        assertThat(appointmentList.getAppointmentList().size(),is(21));
    }

}
