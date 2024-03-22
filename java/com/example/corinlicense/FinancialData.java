package com.example.corinlicense;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialData {
    private final BigDecimal balance;
    private final BigDecimal spentToday;
    private final BigDecimal savings;
    private final Date date;

    public FinancialData(BigDecimal balance, BigDecimal spentToday, BigDecimal savings, Date date) {
        this.balance = balance;
        this.spentToday = spentToday;
        this.savings = savings;
        this.date = date;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getSpentToday() {
        return spentToday;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public Date getDate(){
        return date;
    }

}
