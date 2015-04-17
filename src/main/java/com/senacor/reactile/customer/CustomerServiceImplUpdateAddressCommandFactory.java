package com.senacor.reactile.customer;

/**
 * CustomerServiceImplUpdateAddressCommand Factory
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 17.04.15
 * Time: 10:33
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public interface CustomerServiceImplUpdateAddressCommandFactory {
    CustomerServiceImplUpdateAddressCommand create(CustomerId customerId, Address address);
}
