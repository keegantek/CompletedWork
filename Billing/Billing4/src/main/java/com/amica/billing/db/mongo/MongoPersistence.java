package com.amica.billing.db.mongo;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.db.CachingPersistence;
import com.amica.billing.db.CustomerRepository;
import com.amica.billing.db.InvoiceRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

@Primary
@Component
public class MongoPersistence extends CachingPersistence {

    private CustomerRepository customerRepository;
    private InvoiceRepository invoiceRepository;

    public MongoPersistence(CustomerRepository customerRepository, InvoiceRepository invoiceRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    protected Stream<Customer> readCustomers() {
        return customerRepository.streamAllBy();
    }

    @Override
    protected Stream<Invoice> readInvoices() {
        return invoiceRepository.streamAllBy();
    }

    @Override
    protected void writeCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    protected void writeInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    @Override
    @PostConstruct
    public void load() {
        super.load();
    }
}
