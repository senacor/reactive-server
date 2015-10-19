package com.senacor.reactile.codec;

import com.senacor.reactile.service.account.Account;
import com.senacor.reactile.service.account.AccountId;
import com.senacor.reactile.service.creditcard.CreditCard;
import com.senacor.reactile.service.creditcard.CreditCardId;
import com.senacor.reactile.service.account.Currency;
import com.senacor.reactile.service.account.Transaction;
import com.senacor.reactile.service.account.TransactionId;
import com.senacor.reactile.service.customer.Address;
import com.senacor.reactile.service.customer.Contact;
import com.senacor.reactile.service.customer.Country;
import com.senacor.reactile.service.customer.Customer;
import com.senacor.reactile.service.customer.CustomerId;
import com.senacor.reactile.user.User;
import com.senacor.reactile.user.UserId;
import io.vertx.core.eventbus.EventBus;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Codecs {
    public static void load(EventBus eventBus) {
        registerDomainClasses(eventBus);
        registerValueTypes(eventBus);
        registerCollectionTypes(eventBus);

    }

    private static void registerValueTypes(EventBus eventBus) {
        Stream.of(
                UserId.class,
                CustomerId.class,
                AccountId.class,
                CreditCardId.class,
                TransactionId.class,
                Currency.class
        )
                .forEach(clazz -> {
                    eventBus.unregisterDefaultCodec(clazz);
                    eventBus.registerDefaultCodec(clazz, ValueObjectMessageCodec.from(clazz));
                });
    }

    private static void registerDomainClasses(EventBus eventBus) {
        Stream.of(
                User.class,
                Address.class,
                Contact.class,
                Country.class,
                Customer.class,
                Account.class,
                CreditCard.class,
                Transaction.class
        )
                .forEach(clazz -> {
                    eventBus.unregisterDefaultCodec(clazz);
                    eventBus.registerDefaultCodec(clazz, DomainObjectMessageCodec.from(clazz));
                });
    }

    private static void registerCollectionTypes(EventBus eventBus) {
        Stream.of(
                ArrayList.class
        )
                .forEach(clazz -> {
                    eventBus.unregisterDefaultCodec(clazz);
                    eventBus.registerDefaultCodec(clazz, new ArrayListObjectMessageCodec());
                });
    }
}
