package com.amica.billing;

import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

import static com.amica.billing.TestUtility.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@Log
public class BillingIntegrationTest {
    public static final String SOURCE_FOLDER = "data";

    public Billing billing;

    /**
     * Assure that the necessary folders are in place, and make a copy
     * of the source data files. Install mock objects as listeners for changes.
     */
    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEMP_FOLDER));
        Files.createDirectories(Paths.get(OUTPUT_FOLDER));
        Files.copy(Paths.get(SOURCE_FOLDER, CUSTOMERS_FILENAME),
                Paths.get(TEMP_FOLDER, CUSTOMERS_FILENAME),
                StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get(SOURCE_FOLDER, INVOICES_FILENAME),
                Paths.get(TEMP_FOLDER, INVOICES_FILENAME),
                StandardCopyOption.REPLACE_EXISTING);

        billing = new Billing(TEMP_FOLDER + "/" + CUSTOMERS_FILENAME, TEMP_FOLDER + "/" + INVOICES_FILENAME);
    }

    @Test
    public void testGetInvoicesOrderedByNumber() {
        assertThat(billing.getInvoicesOrderedByNumber(), hasNumbers(101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 121, 122, 123, 124));
    }

    @Test
    public void testGetInvoicesOrderedByDate() {
        assertThat(billing.getInvoicesOrderedByDate(), hasNumbers(101, 102, 103, 104, 110, 105, 106, 107, 109, 111, 112, 113, 108, 114, 115, 116, 117, 118, 119, 121, 123, 122, 124));
    }

    @Test
    public void testGetOverdueInvoices() {
        assertThat(billing.getOverdueInvoices(AS_OF_DATE), hasNumbers(102, 105, 106, 107, 113, 116, 118, 122, 124));
    }

    @Test
    public void testGetInvoicesGroupedByCustomer() {
        Map<Customer, List<Invoice>> invoicesNumbers = billing.getInvoicesGroupedByCustomer();
        assertThat(invoicesNumbers.get(billing.getCustomers().get("Jerry Reed")).stream(), hasNumbers(109, 122));
    }

    @Test
    public void testCreateCustomer() {
        billing.createCustomer("Customer", "Four", Terms.CREDIT_60);
        try (Stream<String> customerLines = Files.lines(Paths.get(TEMP_FOLDER + "/" + CUSTOMERS_FILENAME))) {
            assertThat(customerLines.filter(customer -> customer.equals("Customer,Four,60")).count(), greaterThan(0L));
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Couldn't load from given filenames.", ex);
        }
    }

    @Test
    public void testCreateInvoice() {
        Invoice invoiceCreated = billing.createInvoice("Jerry Reed", 123.45);
        try (Stream<String> invoiceLines = Files.lines(Paths.get(TEMP_FOLDER + "/" + INVOICES_FILENAME))) {
            assertThat(invoiceLines.filter(invoice -> invoice.equals("" + invoiceCreated.getNumber() +",Jerry,Reed,123.45," + invoiceCreated.getIssueDate().toString())).count(), greaterThan(0L));
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Couldn't load from given filenames.", ex);
        }
    }

    @Test
    public void testPayInvoice() {
        Invoice invoiceCreated = billing.createInvoice("Jerry Reed", 123.45);
        billing.payInvoice(invoiceCreated.getNumber());
        System.out.println(invoiceCreated.getPaidDate());
        try (Stream<String> invoiceLines = Files.lines(Paths.get(TEMP_FOLDER + "/" + INVOICES_FILENAME))) {
            assertThat(invoiceLines.filter(invoice -> invoice.equals("" + invoiceCreated.getNumber() +",Jerry,Reed,123.45," + invoiceCreated.getIssueDate().toString() + "," + invoiceCreated.getPaidDate().get())).count(), greaterThan(0L));
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Couldn't load from given filenames.", ex);
        }
    }
}
