package com.amica.billing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {

    public enum Terms {
        CASH(0),
        CREDIT_30(30),
        CREDIT_45(45),
        CREDIT_60(60),
        CREDIT_90(90);

        private int days;

        private Terms(int days) {
            this.days = days;
        }

        public int getDays() {
            return days;
        }
    }

    private String firstName;
    private String lastName;
    private Terms terms;

    public String getName() {
        return firstName + " " + lastName;
    }
}
