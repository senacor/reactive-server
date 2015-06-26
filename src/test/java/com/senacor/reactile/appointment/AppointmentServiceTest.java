package com.senacor.reactile.appointment;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.json.JsonObject;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import rx.observers.TestSubscriber;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

        assertEquals("Consulting 2", appointment.getName());
    }

    @Test
    public void thatAppointmentsAreReturnedByBranch() throws InterruptedException {
        String eventAddress = UUID.randomUUID().toString();
        service.getAppointmentsByBranchObservable("2", eventAddress)
                .subscribe(ea -> System.out.println("Returned: " + ea), e -> e.printStackTrace(), () -> System.out.println("Completed!"));

        MessageConsumer<Object> consumer = vertxRule.vertx().eventBus().consumer(eventAddress);
        Iterable<Appointment> appointments = consumer
                .toObservable()
                .takeWhile(msg -> "next".equals(msg.headers().get("type")))
                .map(msg -> msg.body())
                .cast(JsonObject.class)
                .map(Appointment::fromJson)
                .toBlocking()
                .toIterable();

        int count = 0;
        for (Appointment app : appointments) {
            assertEquals("2", app.getBranchId());
            count++;
        }

        assertEquals(6, count);
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
    public void getAppointmentsByUserTest() {
        final int expectedListSize = 6;
        AppointmentList appointmentList = service.getAppointmentsByUserObservable("aangel").toBlocking().first();

        appointmentList.getAppointmentList().forEach(appointment -> {
            System.out.println(appointment.toJson());
        });

        assertEquals(expectedListSize, appointmentList.getAppointmentList().size());
    }
}
