package com.senacor.reactile.appointment;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Andreas Keefer
 */
public class BranchDatabaseTest {

    private static final BranchDatabase DATABASE = new BranchDatabase();

    @Test
    public void testSaveOrUpdate() throws Exception {
        Branch saved = DATABASE.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());
    }

    @Test
    public void testFindById() throws Exception {
        Branch saved = DATABASE.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());

        Branch byId = DATABASE.findById(saved.getId());
        assertSame(saved, byId);
    }

    @Test
    public void testDeleteById() throws Exception {
        Branch saved = DATABASE.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());

        Branch byId = DATABASE.deleteById(saved.getId());
        assertSame(saved, byId);

        assertNull(DATABASE.findById(saved.getId()));
    }

    @Test
    public void testFindAll() throws Exception {
        Branch saved = DATABASE.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());

        assertThat(DATABASE.findAll(), Matchers.hasItem(saved));
    }
}