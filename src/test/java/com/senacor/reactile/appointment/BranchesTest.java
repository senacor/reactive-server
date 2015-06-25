package com.senacor.reactile.appointment;

import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Country;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BranchesTest {

    Branch bonn = Branch.newBuilder().withId("1").withName("Bonn").withAddress(new Address(null, "Foo Str.", "12345", "6", "Bonn", new Country("Germany", "DE"), 1)).build();

    @Test
    public void testToJson() throws Exception {
        List<Branch> branchListe = Arrays.asList(bonn);
        Branches branches = new Branches(branchListe);

        JsonObject json = branches.toJson();
        System.out.println(json.encodePrettily());
    }
}