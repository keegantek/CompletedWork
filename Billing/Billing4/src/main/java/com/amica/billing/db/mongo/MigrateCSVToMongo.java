package com.amica.billing.db.mongo;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.Terms;
import com.amica.billing.db.CustomerRepository;
import com.amica.billing.db.InvoiceRepository;
import com.amica.billing.db.Migration;
import com.amica.billing.db.Persistence;
import com.amica.billing.parse.ParserPersistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.LocalDate;

@ComponentScan(basePackageClasses={CustomerRepository.class, ParserPersistence.class})
@EnableAutoConfiguration
@EnableMongoRepositories(basePackageClasses=CustomerRepository.class)
@PropertySource(value={"classpath:DB.properties","classpath:migration.properties"})
public class MigrateCSVToMongo {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(MigrateCSVToMongo.class, args) ) {
//            CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
//            System.out.println(customerRepository.count());
//            customerRepository.deleteAll();
//            Customer customer = new Customer("Test", "Customer One Whatever", Terms.CREDIT_30);
//            customerRepository.save(customer);
//            System.out.println(customerRepository.count());
//            System.out.println(customerRepository.findAll());
//            System.out.println(customerRepository.findByFirstNameAndLastName("Test", "Customer One Whatever"));
//
//            InvoiceRepository invoiceRepository = context.getBean(InvoiceRepository.class);
//            System.out.println(invoiceRepository.count());
//            invoiceRepository.deleteAll();
//            Invoice invoice = new Invoice(123, customer, 22.02, LocalDate.now());
//            invoiceRepository.save(invoice);
//            System.out.println(invoiceRepository.count());
//            System.out.println(invoiceRepository.findAll());
//            System.out.println(invoiceRepository.findById(123).get());

//            MongoPersistence mongoPersistence = context.getBean(MongoPersistence.class);
//            System.out.println(mongoPersistence.getCustomers());
//            System.out.println(mongoPersistence.getInvoices());

            Migration migration = context.getBean(Migration.class);
            CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
            System.out.println(customerRepository.count());
            InvoiceRepository invoiceRepository = context.getBean(InvoiceRepository.class);
            System.out.println(invoiceRepository.count());
            migration.migrate();
            System.out.println(customerRepository.count());
            System.out.println(invoiceRepository.count());

        }
    }
}
