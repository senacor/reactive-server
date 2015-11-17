package com.senacor.reactile.service.branch;

import com.senacor.reactile.service.branch.Branch;
import com.senacor.reactile.service.branch.BranchList;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

/**
 * @author Andreas Keefer
 */
public class BranchListTest {

    private static final Logger logger = LoggerFactory.getLogger(BranchListTest.class);

    @Test
    public void testToAndFromJson() throws Exception {
        BranchList branchList = BranchList.newBuilder()
                .withBranches(Arrays.asList(Branch.newBuilder("1").build(),
                        Branch.newBuilder("2").build()))
                .build();

        JsonObject json = branchList.toJson();
        logger.info(json.encodePrettily());
        BranchList.fromJson(json);
        assertThat(branchList.getBranches(), iterableWithSize(2));
    }
}