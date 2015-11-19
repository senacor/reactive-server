package com.senacor.reactile.service.appointment;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import com.senacor.reactile.mongo.MongoInitializer;
import com.senacor.reactile.service.customer.CustomerId;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

import static com.senacor.reactile.domain.IdentityMatchers.hasId;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasProperty;
import static com.senacor.reactile.domain.JsonObjectMatchers.hasValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AppointmentServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);
    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private AppointmentService service;

    private final MongoInitializer mongoInitializer = new MongoInitializer(vertxRule.vertx(), AppointmentServiceImpl.COLLECTION);

    @Test
    public void thatSingleAccountIsReturned_forAccountId() {
        AppointmentId appointmentId = new AppointmentId("app-32423") ;
        mongoInitializer.writeBlocking(AppointmentFixtures.randomAppointment(appointmentId));
        Appointment appointment = service.getAppointmentById(appointmentId).toBlocking().first();
        assertThat(appointment, hasId(appointmentId));
    }

    @Test
    public void thatMultipleAccountsAreReturned_forCustomer() {
        mongoInitializer.writeBlocking(AppointmentFixtures.randomAppointment("app-001", "cust-001"));
        mongoInitializer.writeBlocking(AppointmentFixtures.randomAppointment("app-002", "cust-001"));
        final Observable<AppointmentList> appointmentsByCustomer = service.getAppointmentsByCustomer(new CustomerId("cust-001"));
        final List<Appointment> appointmentList = appointmentsByCustomer.map(appointments -> appointments.getAppointmentList()).toBlocking().first();
        assertThat(appointmentList, hasSize(2));
        // assertThat(appointments, hasItems(hasValue("id", "app-001"), hasValue("id", "app-002")));
    }

    @Test
    public void thatAppointmentCanBeCreated() {
        Appointment appointment = service.createOrUpdateAppointment(AppointmentFixtures.randomAppointment("acc-003", "cust-003")).toBlocking().first();
        assertThat(appointment.toJson(), hasProperty("id"));
        assertThat(appointment.toJson(), hasValue("id", "acc-003"));
        assertThat(appointment.toJson(), hasValue("customerId", "cust-003"));
    }


}