/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.senacor.reactile.appointment;

import com.google.common.collect.ImmutableList;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
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

    public Branches() {
        branches = Collections.emptyList();
    }

    public Branches(final JsonObject json) {
        List<Branch> list =
                unmarshal(json.getJsonArray("branches"), Branch::fromJson);
        this.branches = list;
    }

    public Branches(Collection<Branch> branches) {
        this.branches = ImmutableList.copyOf(branches);
    }

    public Branches(Branches toCopy) {
        this.branches = ImmutableList.copyOf(toCopy.getBranches());
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
