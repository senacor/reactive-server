package com.senacor.reactile.appointment;

import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * @author Andreas Keefer
 */
public class BranchDatabaseTest {

    @ClassRule
    public final static VertxRule vertxRule = new VertxRule();

    @Rule
    public final GuiceRule guiceRule = new GuiceRule(vertxRule.vertx(), this);

    @Inject
    private BranchDatabase database;

    @Test
    public void testSaveOrUpdate() throws Exception {
        Branch saved = database.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());
    }

    @Test
    public void testFindById() throws Exception {
        Branch saved = database.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());

        Branch byId = database.findById(saved.getId());
        assertSame(saved, byId);
    }

    @Test
    public void testDeleteById() throws Exception {
        Branch saved = database.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());

        Branch byId = database.deleteById(saved.getId());
        assertSame(saved, byId);

        assertNull(database.findById(saved.getId()));
    }

    @Test
    public void testFindAll() throws Exception {
        Branch saved = database.saveOrUpdate(Branch.newBuilder().withName("Test").build());
        assertNotNull(saved.getId());

        assertThat(database.findAll(), Matchers.hasItem(saved));
    }
}