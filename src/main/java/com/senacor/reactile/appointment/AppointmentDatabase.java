package com.senacor.reactile.appointment;

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

    private static final DelayService DELAY = new DelayService();
    private boolean delayEnabled = false;

    private static long NEXT_ID = 0;

    private final Map<String, Appointment> dataStore = new ConcurrentHashMap<>();

    public AppointmentDatabase() {
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 1").withBranchId("1").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 2").withBranchId("1").withCustomerId("2").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 3").withBranchId("1").withCustomerId("3").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 4").withBranchId("1").withCustomerId("4").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 5").withBranchId("1").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 6").withBranchId("1").withCustomerId("4").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 7").withBranchId("2").withCustomerId("5").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 8").withBranchId("2").withCustomerId("6").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 9").withBranchId("2").withCustomerId("7").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 10").withBranchId("2").withCustomerId("8").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 11").withBranchId("2").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 12").withBranchId("2").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 13").withBranchId("3").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 14").withBranchId("3").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 15").withBranchId("4").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 16").withBranchId("4").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 17").withBranchId("5").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 18").withBranchId("5").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 19").withBranchId("5").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 20").withBranchId("6").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        saveOrUpdate(Appointment.newBuilder().withName("Consulting 21").withBranchId("6").withCustomerId("1").withStart(ZonedDateTime.now()).withEnd(ZonedDateTime.now().plusHours(1)).build());
        delayEnabled = true;
    }

    public Appointment saveOrUpdate(final Appointment appointment) {
        notNull(appointment, "appointment must not be null");
        final Appointment save;
        if (null == appointment.getId()) {
            save = new Appointment(nextId(),
                    appointment.getName(),
                    appointment.getCustomerId(),
                    appointment.getBranchId(),
                    appointment.getStart(),
                    appointment.getEnd(),
                    appointment.getNote());
        } else {
            save = appointment;
        }
        dataStore.put(save.getId(), save);
        delay(0.5);
        return save;
    }

    public Appointment findById(final String appointmentId) {
        delay(0.5);
        return dataStore.get(appointmentId);
    }

    public Appointment deleteById(final String appointmentId) {
        delay(0.5);
        return dataStore.remove(appointmentId);
    }

    public Collection<Appointment> findAll() {
        delay(2);
        return dataStore.values();
    }

    public List<Appointment> findByCustomerId(String customerId) {
        delay();
        return dataStore.values().stream()
                .filter(appointment -> customerId.equals(appointment.getCustomerId()))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByBranchId(String branchId) {
        delay(1.5);
        return dataStore.values().stream()
                .filter(appointment -> branchId.equals(appointment.getBranchId()))
                .collect(Collectors.toList());
    }

    private static synchronized String nextId() {
        return String.valueOf(NEXT_ID++);
    }

    private void delay() {
        if (delayEnabled) {
            DELAY.delayed();
        }
    }

    private void delay(double faktor) {
        if (delayEnabled) {
            DELAY.delayed(BigDecimal.valueOf(faktor));
        }
    }
}
