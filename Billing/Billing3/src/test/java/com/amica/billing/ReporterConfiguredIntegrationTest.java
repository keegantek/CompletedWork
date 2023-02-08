package com.amica.billing;

import com.amica.billing.ReporterIntegrationTest;
import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import static com.amica.billing.BillingIntegrationTest.SOURCE_FOLDER;
import static com.amica.billing.TestUtility.*;

public class ReporterConfiguredIntegrationTest extends ReporterIntegrationTest {

    public static final String CUSTOMERS_QUOTED = "customers_quoted.csv";
    public static final String INVOICES_QUOTED = "invoices_quoted.csv";

    @BeforeAll
    public static void setUpBeforeAll() {
        System.setProperty("env.name", "Quoted");
        ComponentConfigurationManager.getInstance().initialize();
    }

    @Override
    public String getOutputFolder() {
        return "alternate";
    }

    @BeforeEach
    @Override
    public void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEMP_FOLDER));
        Files.createDirectories(Paths.get(OUTPUT_FOLDER));
        Files.copy(Paths.get(SOURCE_FOLDER, CUSTOMERS_QUOTED),
                Paths.get(TEMP_FOLDER, CUSTOMERS_QUOTED),
                StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get(SOURCE_FOLDER, INVOICES_QUOTED),
                Paths.get(TEMP_FOLDER, INVOICES_QUOTED),
                StandardCopyOption.REPLACE_EXISTING);
        Files.createDirectories(Paths.get(getOutputFolder()));
        Stream.of(Reporter.FILENAME_INVOICES_BY_NUMBER,
                Reporter.FILENAME_INVOICES_BY_CUSTOMER,
                Reporter.FILENAME_OVERDUE_INVOICES,
                Reporter.FILENAME_CUSTOMERS_AND_VOLUME)
                .forEach(f -> new File(getOutputFolder(), f).delete());

        reporter = new Reporter();
        billing = reporter.getBilling();
    }
}
