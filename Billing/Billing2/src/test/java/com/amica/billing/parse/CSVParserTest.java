package com.amica.billing.parse;

import com.amica.billing.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.amica.billing.TestUtility.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.stream.Stream;

/**
 * Unit test for the {@link CSVParser}. Relies on data sets in the 
 * {@link TestUtility} and its own CSV representations of those data sets,
 * help in memory as lists of strings, to drive the parsing and producing
 * methods and expect clean translations between string and object forms.
 * 
 * @author Will Provost
 */
public class CSVParserTest {

	public CSVParser parser;

	@BeforeEach
	public void setUp() {
		parser = new CSVParser();
	}

	public static final List<String> GOOD_CUSTOMER_DATA = Stream.of
			("Customer,One,CASH",
			 "Customer,Two,45",
			 "Customer,Three,30").toList();
	
	public static final List<String> BAD_CUSTOMER_DATA = Stream.of 
			("Customer,One,CASHY_MONEY", 
			 "Customer,Two",
			 "Customer,Three,30").toList();

	public static final List<String> GOOD_INVOICE_DATA = Stream.of 
			("1,Customer,One,100,2022-01-04",
			 "2,Customer,Two,200,2022-01-04,2022-01-05",
			 "3,Customer,Two,300,2022-01-06",
			 "4,Customer,Two,400,2021-11-11",
			 "5,Customer,Three,500,2022-01-04,2022-01-08",
			 "6,Customer,Three,600,2021-12-04").toList();
	
	public static final List<String> BAD_INVOICE_DATA = Stream.of
			("1,Customer,One,100,2022-01-04",
			 "2,Customer,Two,200,2022-01-04,2022-01-05",
			 "3,Customer,Two,300",
			 "4,Customer,Four,400,2021-11-11",
			 "5,Customer,Three,500,2022-01-04,20220108",
			 "6,Customer,Three,600,2021-12-04").toList();

	@Test
	public void testParseGoodCustomers() {
		List<Customer> goodCustomersDataList = parser.parseCustomers(GOOD_CUSTOMER_DATA.stream()).toList();
		List<Customer> goodCustomersList = GOOD_CUSTOMERS.stream().toList();
		assertThat(goodCustomersDataList, sameAsList(goodCustomersList));
	}

	@Test
	public void testParseBadCustomers() {
		List<Customer> badCustomersDataList = parser.parseCustomers(BAD_CUSTOMER_DATA.stream()).toList();
		assertThat(badCustomersDataList, sameAsList(BAD_CUSTOMERS));
	}

	@Test
	public void testParseGoodInvoices() {
		assertThat(parser.parseInvoices(GOOD_INVOICE_DATA.stream(), GOOD_CUSTOMERS_MAP).toList(), sameAsList(GOOD_INVOICES));
	}

	@Test
	public void testParseBadInvoices() {
		assertThat(parser.parseInvoices(BAD_INVOICE_DATA.stream(), GOOD_CUSTOMERS_MAP).toList(), sameAsList(BAD_INVOICES));
	}

	@Test
	public void testProduceCustomers() {
		assertThat(parser.produceCustomers(GOOD_CUSTOMERS.stream()).toList(), sameAsList(GOOD_CUSTOMER_DATA));
	}

	@Test
	public void testProduceInvoices() {
		List<String> strings = parser.produceInvoices(GOOD_INVOICES.stream()).map(str -> str.replaceAll("\\.00", "")).toList();
		assertThat(strings, sameAsList(GOOD_INVOICE_DATA));
	}
}
