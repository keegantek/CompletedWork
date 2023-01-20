package com.amica.billing;

import com.amica.billing.parse.Parser;
import com.amica.billing.parse.ParserFactory;
import lombok.Getter;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Billing {
    private String customersFile;
    private String invoicesFile;
    private Parser parser;
    private List<Invoice> invoices;
    private Map<String, Customer> customers;
    private List<Consumer<Invoice>> invoiceListeners;
    private List<Consumer<Customer>> customerListeners;

    public Billing(String customersFile, String invoicesFile) {
        this.customersFile = customersFile;
        this.invoicesFile = invoicesFile;
        this.parser = ParserFactory.createParser(customersFile);

        try (FileInputStream fileToRead = new FileInputStream(customersFile)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileToRead));
            this.customers = parser.parseCustomers(reader.lines()).collect(Collectors.toMap(customer -> customer.getName(), customer -> customer));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fileToRead = new FileInputStream(invoicesFile)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileToRead));
            this.invoices = parser.parseInvoices(reader.lines(), this.customers).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.invoiceListeners = new ArrayList<>();
        this.customerListeners = new ArrayList<>();
    }

    public void createCustomer(String firstName, String lastName, Customer.Terms terms) {
        Customer customer = new Customer(firstName, lastName, terms);
        customers.put(firstName + " " + lastName, customer);
        saveCustomers();

        for (Consumer<Customer> customerListener : customerListeners) {
            customerListener.accept(customer);
        }
    }

    public void createInvoice(String customerName, double amount) {
        List<Invoice> invoicesByNumber = getInvoicesOrderedByNumber().collect(Collectors.toList());
        Invoice invoice = new Invoice(
                invoicesByNumber.get(invoicesByNumber.size() - 1).getNumber() + 1,
                customers.get(customerName),
                amount,
                LocalDate.now(),
                Optional.empty()
                );
        invoices.add(invoice);
        saveInvoices();

        for (Consumer<Invoice> invoiceListener : invoiceListeners) {
            invoiceListener.accept(invoice);
        }
    }

    public Map<String, Customer> getCustomers() {
        return Collections.unmodifiableMap(this.customers);
    }

    public List<Invoice> getInvoices() {
        return Collections.unmodifiableList(this.invoices);
    }

    /**
     * Retrieves all Invoices, sorted by their number
     *
     * @return a Stream of Invoices sorted by number
     */
    public Stream<Invoice> getInvoicesOrderedByNumber() {
        return getInvoices().stream().sorted(Comparator.comparingInt(Invoice::getNumber));
    }

    /**
     * Retrieves all Invoices for a particular Customer, sorted by number
     *
     * @return a Stream of Invoices for a particular Customer, sorted by number
     */
    public Stream<Invoice> getInvoicesForCustomerOrderedByNumber(Customer customer) {
        return getInvoicesOrderedByNumber().filter(i -> i.getCustomer().equals(customer));
    }

    /**
     * Retrieves all overdue Invoices, sorted by issued date
     *
     * @return a Stream of overdue Invoices, sorted by issued date
     */
    public Stream<Invoice> getOverdueInvoicesOrderedByInvoiceDate() {
        return getInvoices().stream().filter(i -> (
                /*
                conditions:
                1. not paid and now is after issued date + term OR
                2. paid and paid date is after issued date + term
                 */
                (!i.getPaidDate().isPresent() && (Duration.between(i.getInvoiceDate().atStartOfDay(), LocalDate.now().atStartOfDay()).toDays() > i.getCustomer().getTerms().getDays()))
                ||
                (i.getPaidDate().isPresent() && (Duration.between(i.getInvoiceDate().atStartOfDay(), i.getPaidDate().get().atStartOfDay()).toDays() > i.getCustomer().getTerms().getDays()))
        )).sorted(Comparator.comparing(Invoice::getInvoiceDate));
    }

    public Invoice getInvoiceByNumber(int number) {
        List<Invoice> invoicesList = getInvoicesOrderedByNumber().filter(i -> i.getNumber() == number).collect(Collectors.toList());

        if (invoicesList.size() > 0) {
            return invoicesList.get(0); // get the first result - there should only be 1
        } else {
            return null;
        }
    }

    /**
     * Saves current Map of Customers to File
     * Derives streams of customers, passes them to the producer, writes resulting stream of Strings to file
     */
    public void saveCustomers() {
        Stream<String> customerString = parser.produceCustomers(customers.values().stream());

        try (PrintWriter out = new PrintWriter(new FileWriter(customersFile))) {
            customerString.forEach(c -> out.println(c));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Saves current List of Invoices to File
     * Derives streams of invoices, passes them to the producer, writes resulting stream of Strings to file
     */
    public void saveInvoices() {
        Stream<String> invoicesString = parser.produceInvoices(invoices.stream());

        try (PrintWriter out = new PrintWriter(new FileWriter(invoicesFile))) {
            invoicesString.forEach(i -> out.println(i));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void payInvoice(int number) {
        Invoice invoice = getInvoiceByNumber(number);
        invoice.setPaidDate(java.util.Optional.of(LocalDate.now()));
        saveInvoices();


        for (Consumer<Invoice> invoiceListener : invoiceListeners) {
            invoiceListener.accept(invoice);
        }
    }

    public void addInvoiceListener(Consumer<Invoice> invoiceListener) {
        invoiceListeners.add(invoiceListener);
    }

    public void removeInvoiceListener(Consumer<Invoice> invoiceListener) {
        invoiceListeners.remove(invoiceListener);
    }

    public void addCustomerListener(Consumer<Customer> customerListener) {
        customerListeners.add(customerListener);
    }

    public void removeCustomerListener(Consumer<Customer> customerListener) { customerListeners.remove(customerListener); }
}