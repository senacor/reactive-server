package com.senacor.reactile.service.appointment;

import com.senacor.reactile.magic.Throttler;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents a Database with some service methods to query Appointments
 *
 * @author Andreas Keefer
 */
public class AppointmentDatabase {

    private final Throttler delay = new Throttler();
    private boolean delayEnabled = false;

    private long nextId = 0;

    private final Map<String, Appointment> dataStore = new ConcurrentHashMap<>();

    public AppointmentDatabase() {
        ZonedDateTime start = ZonedDateTime.of(2005, 11, 19, 16, 0, 0, 0, ZoneId.systemDefault());
        saveOrUpdate(Appointment.newBuilder().withId("1").withName("Consulting 1").withBranchId("1").withCustomerId("cust-100000").withUserId("momann").withStart(start).withEnd(start.plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("2").withName("Consulting 2").withBranchId("1").withCustomerId("cust-100002").withUserId("rwinzinger").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("3").withName("Consulting 3").withBranchId("1").withCustomerId("cust-100003").withUserId("mmenzel").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("4").withName("Consulting 4").withBranchId("1").withCustomerId("cust-100004").withUserId("akeefer").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("5").withName("Account 1").withBranchId("1").withCustomerId("cust-100001").withUserId("aloch").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("6").withName("Consulting 6").withBranchId("1").withCustomerId("cust-100004").withUserId("aloch").withStart(ZonedDateTime.now().plusHours(1)).withEnd(ZonedDateTime.now().plusHours(2)).build());
        saveOrUpdate(Appointment.newBuilder().withId("7").withName("Consulting 7").withBranchId("2").withCustomerId("cust-100005").withUserId("adick").withStart(ZonedDateTime.now().minusHours(4)).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("8").withName("Consulting 8").withBranchId("2").withCustomerId("cust-100006").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("9").withName("Consulting 9").withBranchId("2").withCustomerId("cust-100007").withUserId("aangel").withStart(ZonedDateTime.now().plusHours(1)).withEnd(ZonedDateTime.now().plusHours(3)).build());
        saveOrUpdate(Appointment.newBuilder().withId("10").withName("Question 1").withBranchId("2").withCustomerId("cust-100008").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("11").withName("Consulting 11").withBranchId("2").withCustomerId("cust-100000").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("12").withName("Consulting 12").withBranchId("2").withCustomerId("cust-100001").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("13").withName("Consulting 13").withBranchId("3").withCustomerId("cust-100001").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("14").withName("Consulting 14").withBranchId("3").withCustomerId("cust-100001").withUserId("cstar").withStart(ZonedDateTime.now().plusHours(2)).withEnd(ZonedDateTime.now().plusHours(4)).build());
        saveOrUpdate(Appointment.newBuilder().withId("15").withName("Consulting 15").withBranchId("4").withCustomerId("cust-100001").withUserId("adick").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("16").withName("Cal 1").withBranchId("4").withCustomerId("cust-100001").withUserId("aloch").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("17").withName("Consulting 17").withBranchId("5").withCustomerId("cust-100001").withUserId("aloch").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("18").withName("Consulting 18").withBranchId("5").withCustomerId("cust-100000").withUserId("akeefer").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("19").withName("Consulting 19").withBranchId("5").withCustomerId("cust-100001").withUserId("mmenzel").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(5)).build());
        saveOrUpdate(Appointment.newBuilder().withId("20").withName("Consulting 20").withBranchId("6").withCustomerId("cust-100001").withUserId("rwinzinger").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withId("21").withName("Sales 1").withBranchId("6").withCustomerId("cust-100001").withUserId("momann").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(1)).build());
//        delayEnabled = true;
    }

    public Appointment saveOrUpdate(final Appointment appointment) {
        notNull(appointment, "appointment must not be null");
        final Appointment save;
        if (null == appointment.getId()) {
            save = Appointment.newBuilder(appointment)
                    .withId(nextId())
                    .build();
        } else {
            save = appointment;
        }
        dataStore.put(save.getId(), save);
        delay(0.2);
        return save;
    }

    public Appointment findById(final String appointmentId) {
        delay(0.1);
        return dataStore.get(appointmentId);
    }

    public Appointment deleteById(final String appointmentId) {
        delay(0.2);
        return dataStore.remove(appointmentId);
    }

    public Collection<Appointment> findAll() {
        delay(0.5);
        return dataStore.values();
    }

    public List<Appointment> findByCustomerId(String customerId) {
        delay(0.2);
        return dataStore.values().stream()
                .filter(appointment -> customerId.equals(appointment.getCustomerId()))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByBranchId(String branchId) {
        delay(0.2);
        return dataStore.values().stream()
                .filter(appointment -> branchId.equals(appointment.getBranchId()))
                .collect(Collectors.toList());
    }

    private synchronized String nextId() {
        return String.valueOf(nextId++);
    }

    private void delay(double faktor) {
        if (delayEnabled) {
            delay.delayed(BigDecimal.valueOf(faktor));
        }
    }
}
