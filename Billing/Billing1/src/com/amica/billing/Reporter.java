package com.amica.billing;

import com.sun.javafx.binding.StringFormatter;
import lombok.AllArgsConstructor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reporter {
    private Billing billing;
    private String directory;
    private LocalDate asOf;

    public Reporter(Billing billing, String directory, LocalDate asOf) {
        this.billing = billing;
        this.directory = directory;
        this.asOf = asOf;

        billing.addInvoiceListener(this::onInvoiceChanged);
        billing.addCustomerListener(this::onCustomerChanged);
    }

    public void reportInvoicesOrderedByNumber() {
        try (PrintWriter out = new PrintWriter(new FileWriter(directory + "/invoices_by_number.txt"))) {
            out.println(
                    "Overdue invoices, ordered by issue date\n" +
                    "==============================================================================\n" +
                    "\n" +
                    "       Customer                     Issued      Amount        Paid\n" +
                    "----  ------------------------  ----------  ----------  ----------\n"
            );
            billing.getInvoicesOrderedByNumber().forEach(i -> out.println(String.format(
                    " %s  %s  %s  %s  %s",
                    i.getNumber(),
                    formatField(i.getCustomer().getName(), 24, false),
                    formatField(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(i.getInvoiceDate()), 10, true),
                    formatField("" + String.format("%.2f", i.getAmount()), 10, true),
                    formatField((i.getPaidDate().isPresent()) ? DateTimeFormatter.ofPattern("yyyy-MM-dd").format(i.getPaidDate().get()) : "", 10, true)
                    )));
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void reportInvoicesGroupedByCustomer() {
        try (PrintWriter out = new PrintWriter(new FileWriter(directory + "/invoices_by_customer.txt"))) {
            out.println(
                    "All invoices, grouped by customer and ordered by invoice number\n" +
                    "==============================================================================\n" +
                    "\n" +
                    "       Customer                     Issued      Amount        Paid\n" +
                    "----  ------------------------  ----------  ----------  ----------\n"
            );
            billing.getCustomers().values().stream().forEach(c -> {
                out.println(c.getName());
                billing.getInvoicesForCustomerOrderedByNumber(c).forEach(
                        i -> out.println(
                                String.format(
                                        " %s  %s  %s  %s  %s",
                                        i.getNumber(),
                                        formatField(i.getCustomer().getName(), 24, false),
                                        formatField(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(i.getInvoiceDate()), 10, true),
                                        formatField("" + String.format("%.2f", i.getAmount()), 10, true),
                                        formatField((i.getPaidDate().isPresent()) ? DateTimeFormatter.ofPattern("yyyy-MM-dd").format(i.getPaidDate().get()) : "", 10, true)
                                ))
                );
                out.print("\n");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reportOverdueInvoices() {
        try (PrintWriter out = new PrintWriter(new FileWriter(directory + "/overdue_invoices.txt"))) {
            out.println(
                    "Overdue invoices, ordered by issued date\n" +
                            "==============================================================================\n" +
                            "\n" +
                            "       Customer                     Issued      Amount        Paid         Due\n" +
                            "----  ------------------------  ----------  ----------  ----------  ----------\n"
            );
            billing.getOverdueInvoicesOrderedByInvoiceDate().forEach(i -> out.println(
                String.format(
                        " %s  %s  %s  %s  %s  %s",
                        i.getNumber(),
                        formatField(i.getCustomer().getName(), 24, false),
                        formatField(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(i.getInvoiceDate()), 10, true),
                        formatField("" + String.format("%.2f", i.getAmount()), 10, true),
                        formatField((i.getPaidDate().isPresent()) ? DateTimeFormatter.ofPattern("yyyy-MM-dd").format(i.getPaidDate().get()) : "", 10, true),
                        formatField(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(i.getInvoiceDate().plusDays(i.getCustomer().getTerms().getDays())), 10, true)
                ))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reportCustomersAndVolume() {
        try (PrintWriter out = new PrintWriter(new FileWriter(directory + "/customer_and_volume.txt"))) {
            out.println(
                    "All invoices, grouped by customer and ordered by invoice number\n" +
                            "==============================================================================\n" +
                            "\n" +
                            "Customer                        Volume\n" +
                            "------------------------  ------------\n"
            );
            billing.getCustomers().values().stream().forEach(c -> {
                out.println(
                    String.format("%s  %s",
                            formatField(c.getName(), 24, false),
                        formatField("" + String.format("%,.2f", billing.getInvoicesForCustomerOrderedByNumber(c).reduce(0.0, (partialVolume, i) -> partialVolume + i.getAmount(), Double::sum)), 12, true)
                    )
                );
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatField(String field, int container, boolean rightJustify) {
        // build spaces
        StringBuffer spacesBuffer = new StringBuffer();
        for (int i=0; i < (container - field.length()); i++) {
            spacesBuffer.append(" ");
        }

        if (rightJustify) {
            return spacesBuffer.append(field).toString();
        } else {
            return (new StringBuffer(field)).append(spacesBuffer).toString();
        }
    }

    public void onInvoiceChanged(Invoice invoice) {
        reportInvoicesOrderedByNumber();
        reportInvoicesGroupedByCustomer();
        reportOverdueInvoices();
        reportCustomersAndVolume();
    }

    public void onCustomerChanged(Customer customer) {
        reportInvoicesOrderedByNumber();
        reportInvoicesGroupedByCustomer();
        reportOverdueInvoices();
        reportCustomersAndVolume();
    }
}
