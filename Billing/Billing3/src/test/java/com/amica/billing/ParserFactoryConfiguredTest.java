package com.amica.billing;

import com.amica.billing.parse.FlatParser;
import com.amica.billing.parse.QuotedCSVParser;
import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.amica.billing.ParserFactory.createParser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class ParserFactoryConfiguredTest {
    @BeforeAll
    public static void setUp() {
        System.setProperty("env.name", "Quoted");
        ComponentConfigurationManager.getInstance().initialize();
    }

    @Test
    public void testDefaultParser() {
        assertThat(createParser("any.txt"), instanceOf(FlatParser.class));
    }

    @Test
    public void testQuotedCSVParser() {
        assertThat(createParser("any.csv"), instanceOf(QuotedCSVParser.class));
    }
}
