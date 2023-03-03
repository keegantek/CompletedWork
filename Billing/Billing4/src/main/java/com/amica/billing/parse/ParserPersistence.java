package com.amica.billing.parse;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.ParserFactory;
import com.amica.billing.db.CachingPersistence;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.stream.Stream;

@Component
@Log
public class ParserPersistence extends CachingPersistence {

    @Value("${ParserPersistence.customersFile}")
    @Setter
    private String customersFile;

    @Value("${ParserPersistence.invoicesFile}")
    @Setter
    private String invoicesFile;

    Parser parser;

    @Override
    protected Stream<Customer> readCustomers() {
        try {
            // open customers file
            Stream<String> customerLines = Files.lines(Paths.get(customersFile));

            // use configured parser to read its contents as a stream of customers
            Stream<Customer> customerStream = parser.parseCustomers(customerLines);

            // return that stream to the base-class template method
            return customerStream;
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Error", ex);
            return null;
        }
    }

    @Override
    protected Stream<Invoice> readInvoices() {
        try {
            // open invoices file
            Stream<String> invoiceLines = Files.lines(Paths.get(invoicesFile));

            // use configured parser to read its contents as a stream of invoices
            Stream<Invoice> invoiceStream = parser.parseInvoices(invoiceLines, customers);

            // return that stream to the base-class template method
            return invoiceStream;
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Error", ex);
            return null;
        }
    }

    @Override
    protected void writeCustomer(Customer customer) {
        try (PrintWriter out = new PrintWriter(new FileWriter(Paths.get(customersFile).toFile())); ) {
            parser.produceCustomers(customers.values().stream())
                    .forEach(out::println);
        } catch (Exception ex) {
            log.log(Level.WARNING, ex,
                    () -> "Couldn't open " + customersFile + " in write mode.");
        }
    }

    @Override
    protected void writeInvoice(Invoice invoice) {
        try ( PrintWriter out = new PrintWriter(new FileWriter(Paths.get(invoicesFile).toFile())); ) {
            parser.produceInvoices(invoices.values().stream())
                    .forEach(out::println);
        } catch (Exception ex) {
            log.log(Level.WARNING, ex,
                    () -> "Couldn't open " + invoicesFile + " in write mode.");
        }
    }

    @Override
    @PostConstruct
    public void load() {
        parser = ParserFactory.createParser(customersFile);
        super.load();
    }
}
