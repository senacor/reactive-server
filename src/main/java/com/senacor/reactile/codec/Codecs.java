package com.senacor.reactile.codec;

import com.senacor.reactile.account.Account;
import com.senacor.reactile.account.AccountId;
import com.senacor.reactile.creditcard.CreditCard;
import com.senacor.reactile.creditcard.CreditCardId;
import com.senacor.reactile.account.Currency;
import com.senacor.reactile.account.Transaction;
import com.senacor.reactile.account.TransactionId;
import com.senacor.reactile.customer.Address;
import com.senacor.reactile.customer.Contact;
import com.senacor.reactile.customer.Country;
import com.senacor.reactile.customer.Customer;
import com.senacor.reactile.customer.CustomerId;
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
