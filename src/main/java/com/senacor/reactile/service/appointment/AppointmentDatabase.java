package com.senacor.reactile.service.appointment;

import com.senacor.reactile.mock.DelayService;

import java.math.BigDecimal;
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

    private final DelayService delay = new DelayService();
    private boolean delayEnabled = false;

    private long nextId = 0;

    private final Map<String, Appointment> dataStore = new ConcurrentHashMap<>();

    public AppointmentDatabase() {
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 1").withBranchId("1").withCustomerId("cust-100000").withUserId("momann").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 2").withBranchId("1").withCustomerId("2").withUserId("rwinzinger").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 3").withBranchId("1").withCustomerId("3").withUserId("mmenzel").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 4").withBranchId("1").withCustomerId("4").withUserId("akeefer").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Account 1").withBranchId("1").withCustomerId("1").withUserId("aloch").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 6").withBranchId("1").withCustomerId("4").withUserId("aloch").withStart(ZonedDateTime.now().plusHours(1)).withEnd(ZonedDateTime.now().plusHours(2)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 7").withBranchId("2").withCustomerId("5").withUserId("adick").withStart(ZonedDateTime.now().minusHours(4)).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 8").withBranchId("2").withCustomerId("6").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 9").withBranchId("2").withCustomerId("7").withUserId("aangel").withStart(ZonedDateTime.now().plusHours(1)).withEnd(ZonedDateTime.now().plusHours(3)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Question 1").withBranchId("2").withCustomerId("8").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 11").withBranchId("2").withCustomerId("cust-100000").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 12").withBranchId("2").withCustomerId("1").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 13").withBranchId("3").withCustomerId("1").withUserId("aangel").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 14").withBranchId("3").withCustomerId("1").withUserId("cstar").withStart(ZonedDateTime.now().plusHours(2)).withEnd(ZonedDateTime.now().plusHours(4)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 15").withBranchId("4").withCustomerId("1").withUserId("adick").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Cal 1").withBranchId("4").withCustomerId("1").withUserId("aloch").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 17").withBranchId("5").withCustomerId("1").withUserId("aloch").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 18").withBranchId("5").withCustomerId("cust-100000").withUserId("akeefer").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 19").withBranchId("5").withCustomerId("1").withUserId("mmenzel").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(5)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 20").withBranchId("6").withCustomerId("1").withUserId("rwinzinger").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Sales 1").withBranchId("6").withCustomerId("1").withUserId("momann").withStart(ZonedDateTime.now().minusHours(1)).withEnd(ZonedDateTime.now().plusHours(1)).build());
        delayEnabled = true;
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
