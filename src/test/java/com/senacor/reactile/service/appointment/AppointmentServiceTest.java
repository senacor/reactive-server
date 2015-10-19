package com.senacor.reactile.service.appointment;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class AppointmentServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentSerivce);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private com.senacor.reactile.rxjava.appointment.AppointmentService appointmentService;

    @Test
    public void getAllAppointmentsTest() {
        AppointmentList appointmentList = appointmentService.getAllAppointmentsObservable()
                .toBlocking().first();

        assertNotNull("AppointmentList", appointmentList);
        assertThat("AppointmentList", appointmentList.getAppointmentList(), not(empty()));
    }

    @Test
    public void thatAppointmentIsReturned() {
        Appointment appointment = appointmentService.createOrUpdateAppointmentObservable(
                Appointment.newBuilder().withName("test").build())
                .toBlocking().first();
        logger.info("created: " + appointment);
        Appointment loaded = appointmentService.getAppointmentByIdObservable(appointment.getId()).toBlocking().single();

        assertEquals("test", loaded.getName());
    }

    @Test
    @Ignore("ist noch nicht ausgereift, da die Events nicht in der Reihenfolge beim Receiver ankommen, " +
            "wie sie auf den EventBus gegeben werden! Es fehlt noch eine SequenceId um die Reihenfolge abzubilden")
    public void thatAppointmentsAreReturnedByBranch() throws InterruptedException {
        String eventAddress = UUID.randomUUID().toString();
        appointmentService.getAppointmentsByBranchObservable("2", eventAddress)
                .subscribe(ea -> System.out.println("Returned: " + ea), e -> e.printStackTrace(), () -> System.out.println("Completed!"));

        MessageConsumer<Object> consumer = vertxRule.vertx().eventBus().consumer(eventAddress);
        Iterable<Appointment> appointments = consumer
                .toObservable()
                .takeWhile(msg -> {
                    String type = msg.headers().get("type");
                    System.out.println("received type=" + type);
                    return "next".equals(type);
                })
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
        Appointment create = Appointment.newBuilder().withUserId(randomNumeric(20))
                .withBranchId(randomNumeric(20))
                .withCustomerId(randomNumeric(20))
                .withName("testCreate")
                .withNote("test create")
                .build();

        Appointment created = appointmentService.createOrUpdateAppointmentObservable(create)
                .toBlocking().single();
        assertThat("id", created.getId(), notNullValue());

        Appointment update = Appointment.newBuilder(created).withName("update").build();

        Appointment updated = appointmentService.createOrUpdateAppointmentObservable(update)
                .toBlocking().single();

        assertEquals(update.toJson(), updated.toJson());

        Appointment readUpdated = appointmentService.getAppointmentByIdObservable(updated.getId())
                .toBlocking().single();

        assertThat(readUpdated.getName(), is("update"));
    }

    @Test
    public void thatAppointmentCanBeDeleted() throws Exception {
        Appointment create = Appointment.newBuilder().withUserId(randomNumeric(20))
                .withBranchId(randomNumeric(20))
                .withCustomerId(randomNumeric(20))
                .withName("testCreate")
                .withNote("test create")
                .build();

        Appointment created = appointmentService.createOrUpdateAppointmentObservable(create)
                .toBlocking().single();
        assertThat("id", created.getId(), notNullValue());

        appointmentService.deleteAppointmentObservable(created.getId()).toBlocking().single();

        try {
            appointmentService.getAppointmentByIdObservable(created.getId()).toBlocking().single();
        } catch (ReplyException re) {
            assertEquals("Appointment with ID "+created.getId()+" doesn't exist.", re.getMessage());
        }
    }

    @Test
    public void getAppointmentsByUserTest() {
        final String userId1 = randomNumeric(20);
        Appointment created1User1 = appointmentService.createOrUpdateAppointmentObservable(
                Appointment.newBuilder().withUserId(userId1).withName("testCreate").build())
            .toBlocking().single();
        Appointment created2User1 = appointmentService.createOrUpdateAppointmentObservable(
                Appointment.newBuilder().withUserId(userId1).withName("testCreate").build())
                .toBlocking().single();
        Appointment created3User2 = appointmentService.createOrUpdateAppointmentObservable(
                Appointment.newBuilder().withUserId(randomNumeric(20)).withName("testCreate").build())
                .toBlocking().single();

        AppointmentList appointmentList = appointmentService.getAppointmentsByUserObservable(userId1)
                .toBlocking().first();

        assertThat(appointmentList, notNullValue());
        assertThat(appointmentList.getAppointmentList(), hasSize(2));
        assertThat(appointmentList.getAppointmentList(), everyItem(hasProperty("userId", is(userId1))));
    }

    @Test
    public void getAppoinmentByCustomerTest() {
        final String customerId1 = randomNumeric(20);
        Appointment created1User1 = appointmentService.createOrUpdateAppointmentObservable(
                Appointment.newBuilder().withCustomerId(customerId1).withName("testCreate").build())
                .toBlocking().single();
        Appointment created2User1 = appointmentService.createOrUpdateAppointmentObservable(
                Appointment.newBuilder().withCustomerId(customerId1).withName("testCreate").build())
                .toBlocking().single();
        Appointment created3User2 = appointmentService.createOrUpdateAppointmentObservable(
                Appointment.newBuilder().withCustomerId(randomNumeric(20)).withName("testCreate").build())
                .toBlocking().single();

        AppointmentList appointmentList = appointmentService.getAppointmentsByCustomerObservable(customerId1).toBlocking().first();

        assertThat(appointmentList, notNullValue());
        assertThat(appointmentList.getAppointmentList(), hasSize(2));
        assertThat(appointmentList.getAppointmentList(), everyItem(hasProperty("customerId", is(customerId1))));
    }
}
