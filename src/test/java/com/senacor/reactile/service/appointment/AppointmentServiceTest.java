package com.senacor.reactile.service.appointment;

import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import java.time.LocalDate;

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

        AppointmentList appointmentList = allAppointments.toBlocking().first();
        assertNotNull(appointmentList);
        assertThat(appointmentList.getAppointmentList().size(),is(21));
    }

    @Test
    public void thatFindByIdWorks() throws Exception {
        Observable<Appointment> appointmentById = appointmentService.getAppointmentById("1");

        Appointment appointment = appointmentById.toBlocking().first();
        assertThat(appointment.getId().getId(),is("1"));
    }


    @Test
    public void thatFindByCustomerIdWorks() throws Exception {
        Observable<AppointmentList> appointmentsByCustomer = appointmentService.getAppointmentsByCustomer("cust-100000");

        AppointmentList appointmentList = appointmentsByCustomer.toBlocking().first();
        assertNotNull(appointmentList);
        assertThat(appointmentList.getAppointmentList().size(),is(3));
    }

    @Test
    public void thatFindByBranchIdWorks() throws Exception {
        Observable<AppointmentList> appointmentsByBranch = appointmentService.getAppointmentsByBranch("1");

        AppointmentList appointmentList = appointmentsByBranch.toBlocking().first();
        assertNotNull(appointmentList);
        assertThat(appointmentList.getAppointmentList().size(),is(6));
    }

    @Test
    public void thatFindByBranchIdAndDateWorks() throws Exception {
        Observable<AppointmentList> appointmentListObservable = appointmentService.getAppointmentsByBranchAndDate("1", LocalDate.now().minusDays(1).toEpochDay());

        AppointmentList appointmentList = appointmentListObservable.toBlocking().first();
        assertNotNull(appointmentList);
        assertThat(appointmentList.getAppointmentList().size(),is(0));
    }

}
