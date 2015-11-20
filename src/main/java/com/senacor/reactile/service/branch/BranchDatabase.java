package com.senacor.reactile.service.branch;

import static org.apache.commons.lang3.Validate.notNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.senacor.reactile.magic.Throttler;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.Country;

/**
 * Represents a Database with some service methods to query Branches
 *
 * @author Andreas Keefer
 */
public class BranchDatabase {

    private final Throttler delay = new Throttler();
    private boolean delayEnabled = false;

    private final Random rnd = new Random();

    private long nextId = 0;

    private final Map<String, Branch> dataStore = new ConcurrentHashMap<>();

    public BranchDatabase() {
        saveOrUpdate(Branch.newBuilder("1").withName("Bonn").withAddress(new Address(null, "Foo Str.", "12345", "6", "Bonn", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("2").withName("Munich").withAddress(new Address(null, "Bar Str.", "12346", "66", "Munich", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("3").withName("Stuttgart").withAddress(new Address(null, "FooBar Str.", "12347", "1", "Stuttgart", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("4").withName("Berlin").withAddress(new Address(null, "Hof Str.", "12348", "2", "Berlin", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("5").withName("Hamburg").withAddress(new Address(null, "Bau Str.", "12349", "7", "Hamburg", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("6").withName("Nürnberg").withAddress(new Address(null, "Hafen Str.", "12340", "8", "Nürnberg", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("7").withName("Frankfurt").withAddress(new Address(null, "Moor Str.", "12355", "9", "Frankfurt", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("8").withName("Leipzig").withAddress(new Address(null, "Gold Str.", "12365", "61", "Leipzig", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("9").withName("Dresden").withAddress(new Address(null, "Pech Str.", "12375", "62", "Dresden", new Country("Germany", "DE"), 1)).build());
        saveOrUpdate(Branch.newBuilder("10").withName("Hof").withAddress(new Address(null, "Mond Str.", "12385", "63", "Hof", new Country("Germany", "DE"), 1)).build());
//        delayEnabled = true;
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

    public String randomExistingID() {
        int n = 0;
        int i = rnd.nextInt(dataStore.size());
        for (String id : dataStore.keySet()) {
            if (n++ == i) {
                return id;
            }
        }
        return null;
    }
}
