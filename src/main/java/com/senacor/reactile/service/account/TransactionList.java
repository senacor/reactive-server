package com.senacor.reactile.service.account;

import com.google.common.collect.ImmutableList;
import com.senacor.reactile.domain.Jsonizable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.senacor.reactile.json.JsonObjects.marshal;
import static com.senacor.reactile.json.JsonObjects.unmarshal;

/**
 * @author mhaunolder
 */
@DataObject
public class TransactionList implements Jsonizable {

    private final List<Transaction> transactionList;

    public TransactionList() {
        this.transactionList = Collections.emptyList();
    }

    public TransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList == null ? Collections.emptyList() : ImmutableList.copyOf(transactionList);
    }

    public TransactionList(TransactionList transactionList) {
        this.transactionList = ImmutableList.copyOf(transactionList.getTransactionList());
    }

    public TransactionList(JsonObject jsonObject) {
        this.transactionList = fromJson(jsonObject).getTransactionList();
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public static TransactionList fromJson(JsonObject jsonObject) {
        checkArgument(jsonObject != null);
        return new TransactionList(unmarshal(jsonObject.getJsonArray("transactionList"), Transaction::fromJson));
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put("transactionList", marshal(transactionList, Transaction::toJson));
    }
}
