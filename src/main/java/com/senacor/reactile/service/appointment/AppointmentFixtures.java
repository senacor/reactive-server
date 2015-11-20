package com.senacor.reactile.service.appointment;

import com.senacor.reactile.service.account.Account;
import com.senacor.reactile.service.account.AccountId;
import com.senacor.reactile.service.customer.CustomerId;
import rx.Observable;
import rx.functions.Func2;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

import static rx.Observable.just;
import static rx.Observable.zip;

public final class AppointmentFixtures {
    private final static Random rd = new Random();

    private AppointmentFixtures() {
    }

    public static Appointment newAppointment1() {
        return randomAppointment()
                .withId("08-cust-15-app-1")
                .withCustomerId("08-cust-1")
                .withBranchId("08-branch-1")
                .withNote("08-note-1")
                .build();
    }

    public static Appointment newAppointmentForCustomer(String customerId) {
        return randomAppointment()
                .withId("08-cust-15-app-1")
                .withCustomerId(customerId)
                .withBranchId("08-branch-1")
                .withNote("08-note-1")
                .build();
    }

    public static Appointment newAppointment2() {
        return randomAppointment()
                .withId("08-cust-15-app-2")
                .withCustomerId("08-cust-2")
                .withBranchId("08-branch-2")
                .withNote("08-note-2")
                .build();
    }

    public static Appointment randomAppointment(AppointmentId appointmentId) {
        return randomAppointment()
                .withId(appointmentId.getId())
                .build();
    }

    public static Appointment randomAppointment(String appointmentId, String customerId) {
        return randomAppointment()
                .withId(appointmentId)
                .withCustomerId(customerId)
                .build();
    }

    public static Appointment.Builder randomAppointment() {
        return Appointment.newBuilder()
                .withId("app-" + uuid())
                .withName("name-" + uuid())
                .withBranchId("branchId-" + uuid())
                .withUserId("userId-" + uuid())
                .withStart(ZonedDateTime.now())
                .withEnd(ZonedDateTime.now())
                .withCustomerId("cust-" + uuid())
                .withNote("note-" + uuid());
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }

    private static Observable<BigDecimal> balance() {
        return just(new BigDecimal(rd.nextInt(10000) - 5000));
    }

    private static Observable<Integer> random(int bound) {
        return Observable.range(1, rd.nextInt(bound) + 1);
    }

}
