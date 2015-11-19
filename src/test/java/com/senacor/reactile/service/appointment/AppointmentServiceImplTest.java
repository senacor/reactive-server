package com.senacor.reactile.service.appointment;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import static org.junit.Assert.assertEquals;

public class AppointmentServiceImplTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);


    @Inject
    private AppointmentService appointmentService;

    @Test
    public void shouldReturnAppointmentForId() {
        Observable<Appointment> appointment = appointmentService.getAppointmentById("1");

        Appointment a = appointment.toBlocking().first();
        assertEquals("1", a.getId());
    }

    @Test
    public void shouldReturnAppointmentsForBranchAndDate() {
        Long date = System.currentTimeMillis();
        appointmentService.getAppointmentsByBranchAndDate("8", date)
        .subscribe(p -> {
            System.out.println(p);
            assertEquals("1", p.getId());
        });
    }

}