package com.senacor.reactile.service.appointment;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import javax.inject.Inject;

public class AppointmentServiceTest{

    private List<Appointment> mocks = Lists.newArrayList();

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);
    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);
    @Mock
    private AppointmentDatabase appointmentDatabase;

    private AppointmentService service;

    @Before
    public void init(){
        initAppointmentMocks();
        initMocks(this);

        service = new AppointmentServiceImpl(appointmentDatabase);
    }

    @Test
    public void thatAppointmentIsFoundById(){
        Appointment appointmentToFind = mocks.get(0);
        when(appointmentDatabase.findById("1")).thenReturn(appointmentToFind);

        Appointment appointment = service.getAppointmentById("1").toBlocking().first();

        assertThat(appointment, is(appointmentToFind));
    }

    @Test
    public void thatAppointmentsAreFoundByBranch(){

        when(appointmentDatabase.findByBranchId("1")).thenReturn(mocks);

        AppointmentList appointmentList = service.getAppointmentsByBranch("1").toBlocking().first();

        assertThat(appointmentList.getAppointmentList(), is(mocks));
    }


    @Test
    public void thatAppointmentsAreFoundByCustomer(){
        when(appointmentDatabase.findByCustomerId("1")).thenReturn(mocks);

        AppointmentList appointmentList = service.getAppointmentsByCustomer("1").toBlocking().first();

        assertThat(appointmentList.getAppointmentList(), is(mocks));
    }

    @Test
    public void thatAllAppointmentsAreFound(){
        when(appointmentDatabase.findAll()).thenReturn(mocks);

        AppointmentList appointmentList = service.getAllAppointments().toBlocking().first();

        assertThat(appointmentList.getAppointmentList(), is(mocks));
    }

    @Test(timeout = 5000)
    public void thatGetAppointmentsByBranchAndDate() {
        final String branchId = "1";
        final Long date = Long.valueOf(ZonedDateTime.now().minusMinutes(5).toEpochSecond());

        when(appointmentDatabase.findAll()).thenReturn(mocks);

        ArrayList<Appointment> appointmentsFound = Lists.newArrayList(service.getAppointmentsByBranchAndDate(
                branchId, date).toBlocking().toIterable());

        assertThat(appointmentsFound.size(), equalTo(1));
    }

    @Test(timeout = 5000)
    public void thatGetAppointmentsByUserAndDate() {
        final String userId = "mmenzel";
        final Long date = Long.valueOf(ZonedDateTime.now().minusMinutes(5).toEpochSecond());

        when(appointmentDatabase.findAll()).thenReturn(mocks);

        ArrayList<Appointment> appointmentsFound = Lists.newArrayList(service.getAppointmentsByUserAndDate
                (userId, date).toBlocking().toIterable());

        assertThat(appointmentsFound.size(), equalTo(1));
    }

    @Test
    public void thatGetAppointmentsByUser() {
        final String userId = "mmenzel";
        when(appointmentDatabase.findAll()).thenReturn(mocks);

        AppointmentList actualResult = service.getAppointmentsByUser(userId).toBlocking().first();

        assertThat(actualResult.getAppointmentList().size(), equalTo(1));
    }


    @Test
    public void thatAppointmentIsCreatedOrUpdated() {
        Appointment appointment = mocks.get(3);
        Appointment resultAppointment = Appointment.newBuilder(appointment).build();

        when(appointmentDatabase.saveOrUpdate(appointment))
                .thenReturn(resultAppointment);

        Appointment createdAppointment = service.createOrUpdateAppointment(appointment).toBlocking().first();

        assertThat(createdAppointment, Matchers.is(resultAppointment));
    }

    @Test
    public void thatAppointmentIsDeleted(){
        Appointment appointmentToDelete = mocks.get(0);
        when(appointmentDatabase.deleteById("1")).thenReturn(appointmentToDelete);

        Appointment deletedAppointment = service.deleteAppointment("1").toBlocking().first();

        assertThat(appointmentToDelete, is(deletedAppointment));
    }

    private void initAppointmentMocks(){
        add(Appointment.newBuilder().withId("1").withName("Consulting 1").withBranchId("1").withCustomerId("cust-100000").withUserId("momann").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        add(Appointment.newBuilder().withId("2").withName("Consulting 2").withBranchId("1").withCustomerId("2").withUserId("rwinzinger").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        add(Appointment.newBuilder().withId("3").withName("Consulting 3").withBranchId("1").withCustomerId("3").withUserId("mmenzel").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(1)).build());
        add(Appointment.newBuilder().withName("Consulting 1").withBranchId("2").withCustomerId("cust-100000")
                .withUserId("momann").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
    }

    private void add(Appointment appointment){
        mocks.add(appointment);
    }

}