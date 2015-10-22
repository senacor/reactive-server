package com.senacor.reactile.service.appointment;


import com.google.common.collect.Lists;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.rxjava.service.appointment.AppointmentService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppointmentServiceTest{

    private List<Appointment> mocks = Lists.newArrayList();

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);
    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);
    @Mock
    private AppointmentDatabase appointmentDatabase;

    private com.senacor.reactile.rxjava.service.appointment.AppointmentService service;

    @Before
    public void init(){
        initAppointmentMocks();
        initMocks(this);
        service = new AppointmentService(new AppointmentServiceImpl(appointmentDatabase));
    }

    @Test
    public void thatAppointmentIsFoundById(){
        Appointment appointmentToFind = mocks.get(0);
        when(appointmentDatabase.findById("1")).thenReturn(appointmentToFind);

        Appointment appointment = service.getAppointmentByIdObservable("1").toBlocking().first();

        assertThat(appointment, is(appointmentToFind));
    }

    @Test
    public void thatAppointmentsAreFoundByBranch(){

        when(appointmentDatabase.findByBranchId("1")).thenReturn(mocks);

        AppointmentList appointmentList = service.getAppointmentsByBranchObservable("1").toBlocking().first();

        assertThat(appointmentList.getAppointmentList(), is(mocks));
    }


    @Test
    public void thatAppointmentsAreFoundByCustomer(){
        when(appointmentDatabase.findByCustomerId("1")).thenReturn(mocks);

        AppointmentList appointmentList = service.getAppointmentsByCustomerObservable("1").toBlocking().first();

        assertThat(appointmentList.getAppointmentList(), is(mocks));
    }

    @Test
    public void thatAllAppointmentsAreFound(){
        when(appointmentDatabase.findAll()).thenReturn(mocks);

        AppointmentList appointmentList = service.getAllAppointmentsObservable().toBlocking().first();

        assertThat(appointmentList.getAppointmentList(), is(mocks));
    }

    @Test
    public void thatAppointmentIsDeleted(){
        Appointment appointmentToDelete = mocks.get(0);
        when(appointmentDatabase.deleteById("1")).thenReturn(appointmentToDelete);

        Appointment deletedAppointment = service.deleteAppointmentObservable("1").toBlocking().first();

        assertThat(appointmentToDelete, is(deletedAppointment));
    }

    private void initAppointmentMocks(){
        add(Appointment.newBuilder().withId("1").withName("Consulting 1").withBranchId("1").withCustomerId("cust-100000").withUserId("momann").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        add(Appointment.newBuilder().withId("2").withName("Consulting 2").withBranchId("1").withCustomerId("2").withUserId("rwinzinger").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        add(Appointment.newBuilder().withId("3").withName("Consulting 3").withBranchId("1").withCustomerId("3").withUserId("mmenzel").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(1)).build());
    }

    private void add(Appointment appointment){
        mocks.add(appointment);
    }

}