package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.rxjava.service.appointment.AppointmentService;
import com.senacor.reactile.service.appointment.Appointment;
import com.senacor.reactile.service.appointment.AppointmentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Arrays;

import static org.mockito.Mockito.when;

/*
 * Project       MKP
 * Copyright (c) 2009,2010,2011 DP IT Services GmbH
 *
 * All rights reserved.
 *
 * $Rev: $ 
 * $Date: $ 
 * $Author: $ 
 */
@RunWith(MockitoJUnitRunner.class)
public class BranchOverviewCommandTest {

  @Mock
  private AppointmentService appointmentService;

  @Test
  public void testBranchOverview() throws Exception {

    when(appointmentService.getAppointmentsByBranchObservable("meine Niederlassung")).thenReturn(
            Observable.just(AppointmentList.newBuilder().withAppointments(Arrays.asList(
                    Appointment.newBuilder().withUserId("Sebastian").build(),
                    Appointment.newBuilder().withUserId("Christian").build(),
                    Appointment.newBuilder().withUserId("Sebastian").build(),
                    Appointment.newBuilder().withUserId("Ernie").build()
                                                                                       )).build()));

    final BranchOverviewCommand subjectUnderTest =
            new BranchOverviewCommand(appointmentService, "meine Niederlassung");

    subjectUnderTest.branchOverview().subscribe(result -> System.out.println(result));

    Thread.sleep(3000);
  }

}