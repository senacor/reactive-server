package com.senacor.reactile.appointment;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.Test;

import java.time.ZonedDateTime;

/**
 * @author Andreas Keefer
 */
public class AppointmentTest {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentTest.class);

    @Test
    public void testToAndFromJson() throws Exception {
        Appointment appointment = Appointment.newBuilder()
                .withId("1")
                .withName("foo")
                .withBranchId("2")
                .withCustomerId("3")
                .withNote("some notes")
                .withStart(ZonedDateTime.now())
                .withEnd(ZonedDateTime.now())
                .build();
        logger.info(appointment);
        JsonObject json = appointment.toJson();
        logger.info(json.encodePrettily());
        Appointment appointmentFromJson = Appointment.fromJson(json);
        logger.info(appointmentFromJson);
    }
}