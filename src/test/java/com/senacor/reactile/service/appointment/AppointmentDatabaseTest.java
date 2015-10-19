package com.senacor.reactile.service.appointment;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;

/**
 * @author Andreas Keefer
 */
public class AppointmentDatabaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentDatabaseTest.class);
    private static final AppointmentDatabase DATABASE = new AppointmentDatabase();

    @Test
    public void testSaveOrUpdate() throws Exception {
        Appointment saved = DATABASE.saveOrUpdate(Appointment.newBuilder().withName("test1").build());
        assertNotNull(saved.getId());
    }

    @Test
    public void testFindById() throws Exception {
        Appointment byId = DATABASE.findById("1");
        assertNotNull("Appointment not found", byId);
        assertEquals("id", "1", byId.getId());
    }

    @Test
    public void testFindAll() throws Exception {
        Appointment saved = DATABASE.saveOrUpdate(Appointment.newBuilder().withName("test1").build());
        Collection<Appointment> all = DATABASE.findAll();
        logger.info(all);
        assertThat(all, hasItem(saved));
    }

    @Test
    public void testFindByCustomerId() throws Exception {
        Appointment saved = DATABASE.saveOrUpdate(Appointment.newBuilder().withName("test1").withCustomerId(RandomStringUtils.randomAlphanumeric(10)).build());
        assertNotNull(saved.getId());
        assertNotNull(saved.getCustomerId());

        List<Appointment> byCustomerId = DATABASE.findByCustomerId(saved.getCustomerId());
        logger.info(byCustomerId);
        assertThat(byCustomerId, contains(saved));
    }

    @Test
    public void testFindByBranchId() throws Exception {
        Appointment saved = DATABASE.saveOrUpdate(Appointment.newBuilder().withName("test1").withBranchId(RandomStringUtils.randomAlphanumeric(10)).build());
        assertNotNull(saved.getId());
        assertNotNull(saved.getBranchId());

        List<Appointment> byBranchId = DATABASE.findByBranchId(saved.getBranchId());
        logger.info(byBranchId);
        assertThat(byBranchId, contains(saved));
    }

    @Test
    public void testDeleteById() throws Exception {
        Appointment saved = DATABASE.saveOrUpdate(Appointment.newBuilder().withName("test1").build());
        assertNotNull(saved.getId());

        saved = DATABASE.deleteById(saved.getId());
        assertNotNull(saved);
        assertNotNull(saved.getId());

        assertNull(DATABASE.findById(saved.getId()));
    }
}