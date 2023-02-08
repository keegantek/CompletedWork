package com.amica.billing;

import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import com.amica.escm.configuration.properties.PropertiesConfiguration;
import org.junit.jupiter.api.BeforeAll;

import java.util.Properties;

import static com.amica.billing.TestUtility.TEMP_FOLDER;

public class BillingPropertyTest extends BillingTest {

    private final String CUSTOMERS_CONFIGURED_FILENAME = "customers_configured.csv";
    private final String INVOICES_CONFIGURED_FILENAME = "invoices_configured.csv";

    @BeforeAll
    public static void setUpBeforeAll() {
        System.setProperty("env.name", "Configured");
        ComponentConfigurationManager.getInstance().initialize();
    }

    @Override
    protected String getCustomersFilename() {
        return CUSTOMERS_CONFIGURED_FILENAME;
    }

    @Override
    protected String getInvoicesFilename() {
        return INVOICES_CONFIGURED_FILENAME;
    }

    @Override
    protected Billing createTestTarget() {
        Properties props = new Properties();
        props.put("Billing.customersFile", TEMP_FOLDER + "/" + getCustomersFilename());
        props.put("Billing.invoicesFile", TEMP_FOLDER + "/" + getInvoicesFilename());
        return new Billing(new PropertiesConfiguration(props));
    }
}
