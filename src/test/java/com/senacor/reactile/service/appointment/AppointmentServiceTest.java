package com.senacor.reactile.service.appointment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import javax.inject.Inject;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;

/**
 * @author Andreas Karoly, Senacor Technologies AG
 */
public class AppointmentServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceTest.class);

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private AppointmentService service;


    @Test
    public void shouldReturnAppointments() {
        AppointmentList appointments = service.getAllAppointments().toBlocking().first();
        assertThat(appointments.getAppointmentList(), is(not(empty())));
    }

}