package com.amica.billing.parse;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static com.amica.billing.ParserFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class ParserFactoryTest {

    private static final String CUSTOMERS_CSV_FILE = "customers.csv";
    private static final String CUSTOMERS_CASE_FLAT_FILE = "customers.FLAT";
    private static final String CUSTOMERS_FLAT_FILE = "customers.flat";
    private static final String CUSTOMERS_TEST_FILE = "customers.test";

    Parser mockParser = mock(Parser.class);
    Supplier<Parser> mockParserFactory = () -> mockParser;

    @Test
    public void testCreateCSVParser() {
        assertThat(createParser(CUSTOMERS_CSV_FILE), instanceOf(CSVParser.class));
    }

    @Test
    public void testCreateFlatParser() {
        assertThat(createParser(CUSTOMERS_FLAT_FILE), instanceOf(FlatParser.class));
    }

    @Test
    public void testCreateCaseInsensitiveParser() {
        assertThat(createParser(CUSTOMERS_CASE_FLAT_FILE), instanceOf(FlatParser.class));
    }

    @Test
    public void testCreateDefaultParser() {
        addParser("test", mockParserFactory);
        assertThat(createParser(CUSTOMERS_TEST_FILE), equalTo(mockParser));
    }

    @Test
    public void testReplaceDefaultParser() {
        replaceParser("csv", mockParserFactory);
        assertThat(createParser(CUSTOMERS_CSV_FILE), equalTo(mockParser));
        resetParsers();
    }

    @Test
    public void testAddParserRejectsOverwrite() {
        assertThrows(IllegalArgumentException.class, () -> addParser("csv", mockParserFactory));
    }

    @Test
    public void testReplaceParserRejectsNew() {
        assertThrows(IllegalArgumentException.class, () -> replaceParser("abc", mockParserFactory));
    }
}
