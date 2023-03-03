package com.amica.billing.db;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.db.mongo.MongoPersistence;
import com.amica.billing.parse.ParserPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Migration {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ParserPersistence source;

    @Autowired
    private MongoPersistence target;

    public void migrate() {
        invoiceRepository.deleteAll();
        customerRepository.deleteAll();

        source.load();
        target.load();

        Map<String, Customer> customerMap = source.getCustomers();
        for (Customer customer : customerMap.values()) {
            target.saveCustomer(customer);
        }

        // copy all invoices from source to target
        Map<Integer, Invoice> invoiceMap = source.getInvoices();
        for (Invoice invoice : invoiceMap.values()) {
            target.saveInvoice(invoice);
        }
    }
}
