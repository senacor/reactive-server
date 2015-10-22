package com.senacor.reactile.service.branch;

import com.senacor.reactile.service.branch.Branch;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.Country;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.Test;

/**
 * @author Andreas Keefer
 */
public class BranchTest {

    private static final Logger logger = LoggerFactory.getLogger(BranchTest.class);

    @Test
    public void testToAndFromJson() throws Exception {
        Branch branch = Branch.newBuilder()
                .withId("1")
                .withName("foo")
                .withAddress(new Address("co", "str", "12345", "6", "Munich", new Country("Germany", "DE"), 1))
                .build();
        logger.info(branch);
        JsonObject json = branch.toJson();
        logger.info(json.encodePrettily());
        Branch branchFromJson = Branch.fromJson(json);
        logger.info(branchFromJson);
    }

    @Test
    public void testToAndFromJsonWithoutAddress() throws Exception {
        Branch branch = Branch.newBuilder()
                .withId("1")
                .withName("foo")
                .build();
        logger.info(branch);
        JsonObject json = branch.toJson();
        logger.info(json.encodePrettily());
        Branch branchFromJson = Branch.fromJson(json);
        logger.info(branchFromJson);
    }
}