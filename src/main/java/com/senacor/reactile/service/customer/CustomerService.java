package com.senacor.reactile.service.customer;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


/* Interface für den Customerservice

@ProxyGen: generiert die Klassen CustomerserviceVertxEBProxy und CustomerServiceVertxProxyHandler in build\generated-src\...\customer\
CustomerserviceVertxEBProxy: Client
CustomerServiceVertxProxyHandler: Handler zur Registrierung im Verticle. Mappt Nachrichten auf die Methoden einer CostomerService-Instanz
CustomerserviceVertxEBProxy: Client-Implementierung des CustomerService-Interfaces. Parameter der Methodenaufrufe werden nach JSON konvertiert, die Aktion gesetzt und dann versand.

@VertxGen: generiert die Klasse CustomerService in build\generated-src\...rxjava\customer\
Die Klasse dekoriert eine Implementierung von CustomerService um Observable-Methoden
*/


@ProxyGen
@VertxGen
public interface CustomerService {

    String ADDRESS = "CustomerService";

    String ADDRESS_EVENT_UPDATE_ADDRESS = CustomerService.ADDRESS + "#updateAddress";

    void getCustomer(CustomerId customerId, Handler<AsyncResult<Customer>> resultHandler);

    void createCustomer(Customer customer, Handler<AsyncResult<Customer>> resultHandler);

    void updateAddress(CustomerId customerId, Address address, Handler<AsyncResult<Customer>> resultHandler);

    void updateContact(CustomerId customerId, Contact address, Handler<AsyncResult<Customer>> resultHandler);

}
