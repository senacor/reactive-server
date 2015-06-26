/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.senacor.reactile.appointment;

import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Contact;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshal;

/**
 * @author tschapitz
 */
@DataObject
public class Branches implements Jsonizable {

    public List<Branch> branches;

    public Branches(){branches = Collections.EMPTY_LIST;}

    public Branches(final JsonObject json) {
        /*ArrayList<Branch> list = new ArrayList<>();
        final JsonArray array = json.getJsonArray("branches");
        array.forEach((JsonObject branch) -> {
            list.add(Branch.fromJson(branch));
        });
        setBranches(list); */
        List<Branch> list =
                unmarshal(json.getJsonArray("branches"), Branch::fromJson);
        this.branches = list;
    }

    public Branches(Collection<Branch> branches) {
        setBranches(branches);
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = Collections.unmodifiableList(branches);
    }

    public void setBranches(Collection<Branch> branches) {
        this.branches = new ArrayList<>(branches);
    }

    @Override
    public JsonObject toJson() {

        return new JsonObject()
                .put("branches", marshal(branches, Branch::toJson));
    }


}
