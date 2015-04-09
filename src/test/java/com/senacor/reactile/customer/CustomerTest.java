package com.senacor.reactile.customer;

import org.hamcrest.Matchers;
import org.junit.Test;

import static com.senacor.reactile.customer.Customer.addOrReplaceAddress;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CustomerTest {

    @Test
    public void testAddAddress() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();
        assertEquals("addresses count", 1, customer.getAddresses().size());

        Address newAddress = Address.anAddress()
                .withIndex(customer.getAddresses().get(0).getIndex() + 1)
                .withCity("NewCity")
                .build();

        Customer customerWithNewAddress = addOrReplaceAddress(customer, newAddress);

        assertThat(customer.getAddresses(), contains(customer.getAddresses().get(0)));
        assertThat(customerWithNewAddress.getAddresses(), contains(newAddress,
                customer.getAddresses().get(0)));
    }

    @Test
    public void testReplaceAddress() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();
        assertEquals("addresses count", 1, customer.getAddresses().size());

        Address newAddress = Address.anAddress()
                .withIndex(customer.getAddresses().get(0).getIndex())
                .withCity("NewCity")
                .build();

        Customer customerWithNewAddress = addOrReplaceAddress(customer, newAddress);

        assertThat(customer.getAddresses(), contains(customer.getAddresses().get(0)));
        assertThat(customerWithNewAddress.getAddresses(), contains(newAddress));
    }

}