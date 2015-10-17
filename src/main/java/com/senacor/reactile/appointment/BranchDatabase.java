package com.senacor.reactile.appointment;

import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.mock.DelayService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents a Database with some service methods to query Branches
 *
 * @author Andreas Keefer
 */
public class BranchDatabase {

    private final DelayService delay = new DelayService();
    private boolean delayEnabled = false;

    private long nextId = 0;

    private final Map<String, Branch> dataStore = new ConcurrentHashMap<>();

    public BranchDatabase() {
        saveOrUpdate(Branch.newBuilder().withName("Bonn").withAddress(new Address(null, "Foo Str.", "12345", "6", "Bonn", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Munich").withAddress(new Address(null, "Bar Str.", "12346", "66", "Munich", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Stuttgart").withAddress(new Address(null, "FooBar Str.", "12347", "1", "Stuttgart", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Berlin").withAddress(new Address(null, "Hof Str.", "12348", "2", "Berlin", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Hamburg").withAddress(new Address(null, "Bau Str.", "12349", "7", "Hamburg", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Nürnberg").withAddress(new Address(null, "Hafen Str.", "12340", "8", "Nürnberg", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Frankfurt").withAddress(new Address(null, "Moor Str.", "12355", "9", "Frankfurt", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Leipzig").withAddress(new Address(null, "Gold Str.", "12365", "61", "Leipzig", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Dresden").withAddress(new Address(null, "Pech Str.", "12375", "62", "Dresden", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder().withName("Hof").withAddress(new Address(null, "Mond Str.", "12385", "63", "Hof", new Country("Germany", "DE"), 1)).build());
        delayEnabled = true;
    }

    public Branch saveOrUpdate(final Branch branch) {
        notNull(branch, "branch must not be null");
        final Branch save;
        if (null == branch.getId()) {
            save = Branch.newBuilder(branch).withId(nextId()).build();
        } else {
            save = branch;
        }
        dataStore.put(save.getId(), save);
        delay(0.2);
        return save;
    }

    public Branch findById(final String branchId) {
        delay(0.1);
        return dataStore.get(branchId);
    }

    public List<Branch> findByIds(final List<String> branchIds) {
        delay(0.2);
        List<Branch> res = new ArrayList<>(branchIds.size());
        for (String branchId : branchIds) {
            Branch branch = dataStore.get(branchId);
            if (null != branch) {
                res.add(branch);
            }
        }
        return res;
    }

    public Branch deleteById(final String branchId) {
        delay(0.2);
        return dataStore.remove(branchId);
    }

    public List<Branch> findAll() {
        delay(0.5);
        return new ArrayList<>(dataStore.values());
    }

    private synchronized String nextId() {
        return String.valueOf(nextId++);
    }

    private void delay(double faktor) {
        if (delayEnabled) {
            delay.delayed(BigDecimal.valueOf(faktor));
        }
    }
}
