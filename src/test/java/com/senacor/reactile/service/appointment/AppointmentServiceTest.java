package com.senacor.reactile.service.appointment;


import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

public class AppointmentServiceTest{

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);
    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);
    @Inject
    private com.senacor.reactile.rxjava.service.appointment.AppointmentService service;

    @Test
    public void test(){

        System.out.println(service);
    }

}