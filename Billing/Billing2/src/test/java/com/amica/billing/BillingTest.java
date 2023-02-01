package com.amica.billing;

import static com.amica.billing.TestUtility.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit test for the {@link Billing} class.
 * This test focuses on the test data set defined in {@link TestUtillity}
 * and prepared data files that reflect that data. We make a copy of the
 * data files at the start of each test case, create the Billing object
 * to load them, and check its getters and query methods.
 * A few more test cases drive updates to the object, and assure that
 * they are reflected in updates to the staged data files.
 * 
 * @author Will Provost
 */
@Log
public class BillingTest {

	public static final String SOURCE_FOLDER = "src/test/resources/data";

	public Billing billing;

	// define a mock Consumer<Customer> and Consumer<Invoice>
	Consumer<Customer> mockCustomerConsumer = mock(Customer.class);
	Consumer<Invoice> mockInvoiceConsumer = mock(Consumer.class);

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

		// register them as listeners
		billing.addCustomerListener(mockCustomerConsumer); // code that sets up mock listeners
		billing.addInvoiceListener(mockInvoiceConsumer); // code that sets up mock listeners
	}

	@Test
	public void testGetInvoicesOrderedByNumber() {
		assertThat(billing.getInvoicesOrderedByNumber(), hasNumbers(1, 2, 3, 4, 5, 6));
	}

	@Test
	public void testGetInvoicesOrderedByDate() {
		assertThat(billing.getInvoicesOrderedByDate(), hasNumbers(4, 6, 1, 2, 5, 3));
	}

	@Test
	public void testGetInvoicesGroupedByCustomer() {
		Map<Customer, List<Invoice>> invoicesNumbers = billing.getInvoicesGroupedByCustomer();
		assertThat(invoicesNumbers.get(GOOD_CUSTOMERS.get(0)).stream(), hasNumbers(1));
		assertThat(invoicesNumbers.get(GOOD_CUSTOMERS.get(1)).stream(), hasNumbers(2, 3, 4));
		assertThat(invoicesNumbers.get(GOOD_CUSTOMERS.get(2)).stream(), hasNumbers(5, 6));
	}

	@Test
	public void testGetOverdueInvoices() {
		assertThat(billing.getOverdueInvoices(AS_OF_DATE), hasNumbers(4, 6, 1));
	}

	@Test
	public void testCreateCustomer() {
		billing.createCustomer("Customer", "Four", Terms.CREDIT_60);
		try (Stream<String> customerLines = Files.lines(Paths.get(TEMP_FOLDER + "/" + CUSTOMERS_FILENAME))) {
			assertThat(customerLines.filter(customer -> customer.equals("Customer,Four,60")).count(), greaterThan(0L));
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Couldn't load from given filenames.", ex);
		}

		verify(mockCustomerConsumer).onCustomerChanged(any());
	}

	@Test
	public void testCreateInvoice() {
		Invoice invoiceCreated = billing.createInvoice(GOOD_CUSTOMERS.get(0).getName(), 123.45);
		try (Stream<String> invoiceLines = Files.lines(Paths.get(TEMP_FOLDER + "/" + INVOICES_FILENAME))) {
			assertThat(invoiceLines.filter(invoice -> invoice.equals("" + invoiceCreated.getNumber() +",Customer,One,123.45," + invoiceCreated.getIssueDate().toString())).count(), greaterThan(0L));
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Couldn't load from given filenames.", ex);
		}
	}

	@Test
	public void testPayInvoice() {
		Invoice invoiceCreated = billing.createInvoice(GOOD_CUSTOMERS.get(0).getName(), 123.45);
		billing.payInvoice(invoiceCreated.getNumber());
		try (Stream<String> invoiceLines = Files.lines(Paths.get(TEMP_FOLDER + "/" + INVOICES_FILENAME))) {
			assertThat(invoiceLines.filter(invoice -> invoice.equals("" + invoiceCreated.getNumber() +",Customer,One,123.45," + invoiceCreated.getIssueDate().toString() + "," + invoiceCreated.getPaidDate().get())).count(), greaterThan(0L));
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Couldn't load from given filenames.", ex);
		}
	}
	
}
