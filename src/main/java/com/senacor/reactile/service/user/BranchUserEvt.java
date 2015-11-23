package com.senacor.reactile.service.user;

import com.senacor.reactile.json.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class BranchUserEvt implements Jsonizable {

    public BranchUserEvt() {

    }

    public BranchUserEvt(BranchUserEvt branchUserEvt) {

    }

    @Override
    public JsonObject toJson() {
        JsonObject json =  new JsonObject();
        json.put("Event", "BranchUserEvt");
        return json;
    }
}
