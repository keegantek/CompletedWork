package com.amica.billing.parse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;

import lombok.extern.java.Log;

/**
 * A parser that can read a CSV format with certain expected columns.
 * 
 * @author Will Provost
 */
@Log
public class CSVParser implements Parser {

	public static final int CUSTOMER_COLUMNS = 3;
	public static final int CUSTOMER_FIRST_NAME_COLUMN = 0;
	public static final int CUSTOMER_LAST_NAME_COLUMN = 1;
	public static final int CUSTOMER_TERMS_COLUMN = 2;

	public static final int INVOICE_MIN_COLUMNS = 5;
	public static final int INVOICE_NUMBER_COLUMN = 0;
	public static final int INVOICE_FIRST_NAME_COLUMN = 1;
	public static final int INVOICE_LAST_NAME_COLUMN = 2;
	public static final int INVOICE_AMOUNT_COLUMN = 3;
	public static final int INVOICE_DATE_COLUMN = 4;
	public static final int INVOICE_PAID_DATE_COLUMN = 5;

	/**
	 * Helper that can parse one line of comma-separated text in order to
	 * produce a {@link Customer} object.
	 */
	private Customer parseCustomer(String line) {
		String[] fields = line.split(",");
		if (fields.length == CUSTOMER_COLUMNS) {
			try {
				String firstName = fields[CUSTOMER_FIRST_NAME_COLUMN];
				String lastName = fields[CUSTOMER_LAST_NAME_COLUMN];
				String termsString = fields[CUSTOMER_TERMS_COLUMN];

				// convert the terms string to an enum
				Customer.Terms terms = Customer.Terms.valueOf((!termsString.equals("CASH")) ? "CREDIT_".concat(termsString) : termsString);

				// create a customer object and return it
				return new Customer(firstName, lastName, terms);

			} catch (Exception ex) {
				log.warning(() -> 
					"Couldn't parse terms value, skipping customer: "+ line);
			}
		} else {
			log.warning(() -> 
				"Incorrect number of fields, skipping customer: " + line);
		}

		return null;
	}

	/**
	 * Helper that can parse one line of comma-separated text in order to
	 * produce an {@link Invoice} object.
	 */
	private Invoice parseInvoice(String line, Map<String, Customer> customers) {
		DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String[] fields = line.split(",");
		if (fields.length >= INVOICE_MIN_COLUMNS) {
			try {
				int number = Integer.parseInt(fields[INVOICE_NUMBER_COLUMN]);
				String first = fields[INVOICE_FIRST_NAME_COLUMN];
				String last = fields[INVOICE_LAST_NAME_COLUMN];
				double amount = Double.parseDouble
						(fields[INVOICE_AMOUNT_COLUMN]);
				
				LocalDate date = LocalDate.parse(fields[INVOICE_DATE_COLUMN], parser);
				Optional<LocalDate> paidDate = fields.length > INVOICE_PAID_DATE_COLUMN 
						? Optional.of(LocalDate.parse(fields[INVOICE_PAID_DATE_COLUMN], parser)) 
						: Optional.empty();

				// find the corersponding customer in the map
				Customer customer = customers.get(first + " " + last);

				// create an invoice and return it
				return new Invoice(number, customer, amount, date, paidDate);
			} catch (Exception ex) {
				log.warning(() -> 
					"Couldn't parse values, skipping invoice: " + line);
			}
		} else {
			log.warning(() -> 
				"Incorrect number of fields, skipping invoice: " + line);
		}

		return null;
	}

	/**
	 * Helper to write a CSV representation of one customer.
	 */
	public String formatCustomer(Customer customer) {
		//provide the values to be formatted
		return String.format("%s,%s,%s", customer.getFirstName(), customer.getLastName(), customer.getTerms());
	}
	
	/**
	 * Helper to write a CSV representation of one invoice.
	 */
	public String formatInvoice(Invoice invoice) {
		//provide the values to be formatted
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		System.out.println(invoice.getNumber());
//		System.out.println(invoice.getPaidDate());
//		System.out.println((invoice.getPaidDate().isPresent()) ? "Paid " + invoice.getPaidDate().get().format(dateFormatter) : "Not Paid");
		return String.format("%d,%s,%s,%.2f,%s,%s", // added , between last 2 %s%s
				invoice.getNumber(),
				invoice.getCustomer().getFirstName(),
				invoice.getCustomer().getLastName(),
				invoice.getAmount(),
				invoice.getInvoiceDate().format(dateFormatter),
				((invoice.getPaidDate().isPresent()) ? invoice.getPaidDate().get().format(dateFormatter) : "")
		);
	}

	@Override
	public Stream<Customer> parseCustomers(Stream<String> customerLines) {
		return customerLines.map(c -> parseCustomer(c));
	}

	@Override
	public Stream<Invoice> parseInvoices(Stream<String> invoiceLines, Map<String, Customer> customers) {
		return invoiceLines.map(i -> parseInvoice(i, customers));
	}

	@Override
	public Stream<String> produceCustomers(Stream<Customer> customers) {
		return customers.map(c -> formatCustomer(c));
	}

	@Override
	public Stream<String> produceInvoices(Stream<Invoice> invoices) {
		return invoices.map(i -> formatInvoice(i));
	}
}
