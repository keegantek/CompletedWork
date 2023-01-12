package com.amica.billing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Invoice {
    @NonNull private int number;
    @NonNull private Customer customer;
    @NonNull private double amount;
    @NonNull private LocalDate invoiceDate;
    private Optional<LocalDate> paidDate;
}
