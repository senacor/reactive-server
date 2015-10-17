package com.senacor.reactile.gateway.commands;

import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.CustomerId;

/**
 * Factory for CustomerUpdateAddressCommand
 * <p>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 16.04.15
 * Time: 15:58
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public interface CustomerUpdateAddressCommandFactory {
    CustomerUpdateAddressCommand create(CustomerId customerId, Address address);
}
