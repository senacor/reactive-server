package com.senacor.reactile.appointment;

import com.google.common.collect.Lists;
import com.senacor.reactile.Services;
import com.senacor.reactile.VertxRule;
import com.senacor.reactile.guice.GuiceRule;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.contains;
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
    public void testFindByIds() throws Exception {
        Branch saved1 = database.saveOrUpdate(Branch.newBuilder().withName("Test1").build());
        assertNotNull(saved1.getId());
        Branch saved2 = database.saveOrUpdate(Branch.newBuilder().withName("Test2").build());

        List<Branch> res = database.findByIds(newArrayList(saved1.getId(), "gibtsNicht", saved2.getId()));
        assertThat(res, contains(saved1, saved2));
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