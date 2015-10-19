package com.senacor.reactile.service.customer;

import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerFixtures;
import org.junit.Test;

import static com.senacor.reactile.service.customer.Customer.addOrReplaceAddress;
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
        assertThat(customerWithNewAddress.getAddresses(), contains(customer.getAddresses().get(0), newAddress));
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

    @Test
    public void testReplaceAddressInList() throws Exception {
        Customer customer = CustomerFixtures.randomCustomer();
        assertEquals("addresses count", 1, customer.getAddresses().size());
        Address address1 = customer.getAddresses().get(0);
        Address address2 = CustomerFixtures.randomAddressBuilder().withIndex(2).build();
        customer = addOrReplaceAddress(customer, address2);
        Address address3 = CustomerFixtures.randomAddressBuilder().withIndex(3).build();
        customer = addOrReplaceAddress(customer, address3);


        Address newAddress = Address.anAddress()
                .withIndex(address2.getIndex())
                .withCity("NewCity")
                .build();

        Customer customerWithNewAddress = addOrReplaceAddress(customer, newAddress);

        assertThat(customer.getAddresses(), contains(address1, address2, address3));
        assertThat(customerWithNewAddress.getAddresses(), contains(address1, newAddress, address3));
    }
}