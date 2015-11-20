package com.senacor.reactile.service.appointment;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * @author Alasdair Collinson, Senacor Technologies AG
 */
@RunWith(JUnitParamsRunner.class)
public class AppointmentServiceTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule(Services.AppointmentService);

    public static final String ID = "22";
    public static final String NAME = "Techies 1";
    public static final String BRANCH_ID = "3.14";
    public static final String CUSTOMER_ID = "cust-109000";
    public static final String USER_ID = "acollinson";

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private AppointmentService appointmentService;

    private Appointment firstAppointment;

    @Before
    public void setup() {
        firstAppointment = appointmentService.getAppointmentById("1").toBlocking().first();
    }

    @After
    public void teardown() {
        appointmentService.createOrUpdateAppointment(firstAppointment);
    }

    @Test
    public void thatAppointmentsCanBeFound() {
        final AppointmentList appointments = appointmentService.getAllAppointments().toBlocking().first();

        assertThat(appointments).isNotNull();
        assertThat(appointments.getAppointmentList()).hasSize(21);
    }

    @Test
    @Parameters
    public void thatAppointmentCanBeFoundById(String id) {
        final Appointment appointment = appointmentService.getAppointmentById(id).toBlocking().first();

        assertThat(appointment).isNotNull();
        assertThat(appointment.getId()).isEqualTo(id);
    }

    @SuppressWarnings("unused")
    private Collection<String> parametersForThatAppointmentCanBeFoundById() {
        List<String> params = Lists.newArrayList();
        for (int i = 1; i <= 21; i++) {
            params.add(Integer.toString(i));
        }
        return params;
    }

    @Test
    @Parameters
    public void thatAppointmentsCanBeFoundByCustomer(String customerId, int frequency) {
        final AppointmentList appointments = appointmentService.getAppointmentsByCustomer(customerId).toBlocking().first();

        assertThat(appointments).isNotNull();
        assertThat(appointments.getAppointmentList()).hasSize(frequency);
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> StringUtils.equals(appointment.getCustomerId(), customerId), "customer with id " + customerId));
    }

    @SuppressWarnings("unused")
    private Collection<Object[]> parametersForThatAppointmentsCanBeFoundByCustomer() {
        return Arrays.asList( //
            new Object[]{"cust-100000", 3}, //
            new Object[]{"cust-100001", 10}, //
            new Object[]{"cust-100002", 1}, //
            new Object[]{"cust-100003", 1}, //
            new Object[]{"cust-100004", 2}, //
            new Object[]{"cust-100005", 1}, //
            new Object[]{"cust-100005", 1}, //
            new Object[]{"cust-100007", 1});
    }

    @Test
    @Parameters
    public void thatAppointmentsCanBeFoundByBranch(String branchId, int frequency) {
        final AppointmentList appointments = appointmentService.getAppointmentsByBranch(branchId).toBlocking().first();

        assertThat(appointments).isNotNull();
        assertThat(appointments.getAppointmentList()).hasSize(frequency);
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> StringUtils.equals(appointment.getBranchId(), branchId), "branch with id " + branchId));
    }

    @SuppressWarnings("unused")
    private Collection<Object[]> parametersForThatAppointmentsCanBeFoundByBranch() {
        return Arrays.asList( //
            new Object[]{"1", 6}, //
            new Object[]{"2", 6}, //
            new Object[]{"3", 2}, //
            new Object[]{"4", 2}, //
            new Object[]{"5", 3}, //
            new Object[]{"6", 2});
    }

    @Test
    @Parameters
    public void thatAppointmentsCanBeFoundByBranchAndDate(String branchId, long date, int frequency) {
        final ZonedDateTime zonedDate = zonedDateTimeFromMillis(date);
        final AppointmentList appointments = appointmentService.getAppointmentsByBranchAndDate(branchId, date).toBlocking().first();

        assertThat(appointments).isNotNull();
        assertThat(appointments.getAppointmentList()).hasSize(frequency);
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> StringUtils.equals(appointment.getBranchId(), branchId), "branch with id " + branchId));
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> appointment.getStart().isBefore(zonedDate), "start at " + zonedDate));
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> appointment.getEnd().isAfter(zonedDate), "end at " + zonedDate));
    }

    @SuppressWarnings("unused")
    private Collection<Object[]> parametersForThatAppointmentsCanBeFoundByBranchAndDate() {
        return Arrays.asList( //
            new Object[]{"1", ZonedDateTime.now().minusMinutes(30).toInstant().toEpochMilli(), 1}, //
            new Object[]{"2", ZonedDateTime.now().plusMinutes(1).toInstant().toEpochMilli(), 5}, //
            new Object[]{"3", ZonedDateTime.now().plusHours(3).toInstant().toEpochMilli(), 1}, //
            new Object[]{"4", ZonedDateTime.now().minusHours(2).toInstant().toEpochMilli(), 0});
    }

    @Test
    @Parameters
    public void thatAppointmentsCanBeFoundByUser(String userId, int frequency) {
        final AppointmentList appointments = appointmentService.getAppointmentsByUser(userId).toBlocking().first();

        assertThat(appointments).isNotNull();
        assertThat(appointments.getAppointmentList()).hasSize(frequency);
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> StringUtils.equals(appointment.getUserId(), userId), "user with id " + userId));
    }

    @SuppressWarnings("unused")
    private Collection<Object[]> parametersForThatAppointmentsCanBeFoundByUser() {
        return Arrays.asList( //
            new Object[]{"momann", 2}, //
            new Object[]{"rwinzinger", 2}, //
            new Object[]{"mmenzel", 2}, //
            new Object[]{"akeefer", 2}, //
            new Object[]{"aloch", 4}, //
            new Object[]{"adick", 2}, //
            new Object[]{"aangel", 6}, //
            new Object[]{"cstar", 1});
    }

    @Test
    @Parameters
    public void thatAppointmentsCanBeFoundByUserAndDate(String userId, long date, int frequency) {
        final ZonedDateTime zonedDate = zonedDateTimeFromMillis(date);
        final AppointmentList appointments = appointmentService.getAppointmentsByUserAndDate(userId, date).toBlocking().first();

        assertThat(appointments).isNotNull();
        assertThat(appointments.getAppointmentList()).hasSize(frequency);
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> StringUtils.equals(appointment.getUserId(), userId), "user with id " + userId));
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> appointment.getStart().isBefore(zonedDate), "start at " + zonedDate));
        assertThat(appointments.getAppointmentList())
            .are(new Condition<>(appointment -> appointment.getEnd().isAfter(zonedDate), "end at " + zonedDate));
    }

    @SuppressWarnings("unused")
    private Collection<Object[]> parametersForThatAppointmentsCanBeFoundByUserAndDate() {
        return Arrays.asList( //
            new Object[]{"momann", ZonedDateTime.now().minusMinutes(30).toInstant().toEpochMilli(), 1}, //
            new Object[]{"rwinzinger", ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli(), 0}, //
            new Object[]{"mmenzel", ZonedDateTime.now().plusHours(4).toInstant().toEpochMilli(), 1}, //
            new Object[]{"akeefer", ZonedDateTime.now().plusMinutes(30).toInstant().toEpochMilli(), 2}, //
            new Object[]{"aloch", ZonedDateTime.now().minusMinutes(1).toInstant().toEpochMilli(), 0}, //
            new Object[]{"adick", ZonedDateTime.now().minusHours(5).toInstant().toEpochMilli(), 0}, //
            new Object[]{"aangel", ZonedDateTime.now().plusHours(1).plusMinutes(5).toInstant().toEpochMilli(), 1}, //
            new Object[]{"cstar", ZonedDateTime.now().plusHours(3).toInstant().toEpochMilli(), 1});
    }

    @Test
    public void thatAppointmentsCanBeCreated() {
        final int sizeBefore = appointmentService.getAllAppointments().toBlocking().first().getAppointmentList().size();

        final Appointment newAppointment =
            Appointment.newBuilder().withId(ID).withName(NAME).withBranchId(BRANCH_ID).withCustomerId(CUSTOMER_ID).withUserId(USER_ID)
                .withStart(ZonedDateTime.now().minusDays(17)).withEnd(ZonedDateTime.now().plusHours(5)).build();
        appointmentService.createOrUpdateAppointment(newAppointment);

        final AppointmentList appointments = appointmentService.getAllAppointments().toBlocking().first();
        final int sizeAfter = appointments.getAppointmentList().size();
        assertThat(sizeBefore).isEqualTo(sizeAfter - 1);
        assertThat(appointments.getAppointmentList())
            .areExactly(1, new Condition<>(appointment -> ID.equals(appointment.getId()), "new appointment id is included"));
        assertThat(appointments.getAppointmentList())
            .areExactly(1, new Condition<>(appointment -> NAME.equals(appointment.getName()), "new appointment name is included"));
        assertThat(appointments.getAppointmentList()).areExactly(1,
            new Condition<>(appointment -> BRANCH_ID.equals(appointment.getBranchId()), "new appointment branch is included"));
        assertThat(appointments.getAppointmentList()).areExactly(1,
            new Condition<>(appointment -> CUSTOMER_ID.equals(appointment.getCustomerId()), "new appointment customer is included"));
        assertThat(appointments.getAppointmentList())
            .areExactly(1, new Condition<>(appointment -> USER_ID.equals(appointment.getUserId()), "new appointment user is included"));
    }

    @Test
    public void thatAppointmentsCanBeUpdated() {
        final int sizeBefore = appointmentService.getAllAppointments().toBlocking().first().getAppointmentList().size();

        final Appointment newAppointment =
            Appointment.newBuilder().withId("1").withName(NAME).withBranchId(BRANCH_ID).withCustomerId(CUSTOMER_ID).withUserId(USER_ID)
                .withStart(ZonedDateTime.now().minusDays(17)).withEnd(ZonedDateTime.now().plusHours(5)).build();
        appointmentService.createOrUpdateAppointment(newAppointment);

        final AppointmentList appointments = appointmentService.getAllAppointments().toBlocking().first();
        final int sizeAfter = appointments.getAppointmentList().size();
        assertThat(sizeBefore).isEqualTo(sizeAfter);
        assertThat(appointments.getAppointmentList())
            .areNot(new Condition<>(appointment -> ID.equals(appointment.getId()), "new appointment id is included"));
        assertThat(appointments.getAppointmentList())
            .areExactly(1, new Condition<>(appointment -> NAME.equals(appointment.getName()), "new appointment name is included"));
        assertThat(appointments.getAppointmentList()).areExactly(1,
            new Condition<>(appointment -> BRANCH_ID.equals(appointment.getBranchId()), "new appointment branch is included"));
        assertThat(appointments.getAppointmentList()).areExactly(1,
            new Condition<>(appointment -> CUSTOMER_ID.equals(appointment.getCustomerId()), "new appointment customer is included"));
        assertThat(appointments.getAppointmentList())
            .areExactly(1, new Condition<>(appointment -> USER_ID.equals(appointment.getUserId()), "new appointment user is included"));
    }

    @Test
    public void thatAppointmentsCanDeleted() {
        final int sizeBefore = appointmentService.getAllAppointments().toBlocking().first().getAppointmentList().size();

        final Appointment deletedAppointment = appointmentService.deleteAppointment("1").toBlocking().first();

        final AppointmentList appointments = appointmentService.getAllAppointments().toBlocking().first();
        final int sizeAfter = appointments.getAppointmentList().size();
        assertThat(sizeBefore).isEqualTo(sizeAfter + 1);
        assertThat(appointments.getAppointmentList())
            .areNot(new Condition<>(appointment -> deletedAppointment.getId().equals(appointment.getId()), "new appointment id is included"));
    }

    private ZonedDateTime zonedDateTimeFromMillis(Long date) {
        return new Date(date).toInstant().atZone(ZoneId.systemDefault());
    }
}