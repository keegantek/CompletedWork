package com.amica.billing;

import com.amica.escm.configuration.properties.PropertiesConfiguration;

import java.util.Properties;

import static com.amica.billing.TestUtility.TEMP_FOLDER;

public class BillingConfiguredTest extends BillingTest {

    private final String CUSTOMERS_CONFIGURED_FILENAME = "customers_configured.csv";
    private final String INVOICES_CONFIGURED_FILENAME = "invoices_configured.csv";

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
        return new Billing();
    }
}
