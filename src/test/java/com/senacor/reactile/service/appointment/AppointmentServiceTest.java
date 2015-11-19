package com.senacor.reactile.service.appointment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import com.google.common.base.VerifyException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;

/**
 * @author Andreas Karoly, Senacor Technologies AG
 */
@RunWith(JUnitParamsRunner.class)
public class AppointmentServiceTest {

    private AppointmentService service;
    private AppointmentDatabase appointmentDatabase;
    private Appointment appointment;

    @Before
    public void setUp() {
        appointmentDatabase = new AppointmentDatabase();
        service = new AppointmentServiceImpl(appointmentDatabase);

        appointment = initializeAppointment();
    }

    @Test
    public void shouldReturnAppointments() {
        AppointmentList appointments = service.getAllAppointments().toBlocking().first();
        assertThat(appointments.getAppointmentList(), is(not(empty())));
    }

    @Test
    public void shouldReturnSpecificAppointmentById() {
        Appointment fetchedAppointment = service.getAppointmentById(appointment.getId()).toBlocking().first();

        assertThat(fetchedAppointment, is(notNullValue()));
        assertThat(fetchedAppointment.getId(), is(appointment.getId()));
    }

    @Test(expected = VerifyException.class)
    public void shouldThrowExceptionOnInvalidAppointmentId() {
        service.getAppointmentById(null);
    }

    @Test
    public void shouldReturnAppointmentsByCustomerId() {
        AppointmentList fetchedAppointments = service.getAppointmentsByCustomer("cust-10042").toBlocking().first();

        assertThat(fetchedAppointments.getAppointmentList(), hasSize(1));
        assertThat(fetchedAppointments.getAppointmentList().get(0).getId(), is(appointment.getId()));
    }

    @Test(expected = VerifyException.class)
    public void shouldThrowExceptionOnInvalidCustomerId() {
        service.getAppointmentsByCustomer(null);
    }

    @Test
    public void shouldReturnAppointmentsByBranchId() {
        AppointmentList fetchedAppointments = service.getAppointmentsByBranch("42").toBlocking().first();

        assertThat(fetchedAppointments.getAppointmentList(), hasSize(1));
        assertThat(fetchedAppointments.getAppointmentList().get(0).getId(), is(appointment.getId()));
    }

    @Test(expected = VerifyException.class)
    public void shouldThrowExceptionOnInvalidBranchId() {
        service.getAppointmentsByBranch(null);
    }

    private Appointment initializeAppointment() {
        Appointment appointment = Appointment.newBuilder().withId("42")
                .withCustomerId("cust-10042")
                .withBranchId("42")
                .build();
        appointmentDatabase.saveOrUpdate(appointment);

        return appointment;
    }
}