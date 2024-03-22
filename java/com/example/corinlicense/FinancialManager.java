package com.example.corinlicense;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialManager {

    private final DatabaseHelper dbHelper;
    private FinancialData financialData;

    public FinancialManager(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        loadFinancialData();
    }

    private void loadFinancialData() {
        this.financialData = dbHelper.getFinancialData();
        if (this.financialData == null) {
            this.financialData = new FinancialData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new Date());
        }
    }

    private void updateDatabase() {
        dbHelper.saveFinancialData(financialData.getBalance(), financialData.getSpentToday(), financialData.getSavings(), financialData.getDate());
    }

    public void addToBalance(BigDecimal amount) {
        financialData = new FinancialData(
                financialData.getBalance().add(amount),
                financialData.getSpentToday(),
                financialData.getSavings(),
                financialData.getDate()
        );
        updateDatabase();
    }
    public boolean withdrawFromBalance(BigDecimal amount) {
        if (financialData.getBalance().compareTo(amount) >= 0) {
            financialData = new FinancialData(
                    financialData.getBalance().subtract(amount),
                    financialData.getSpentToday().add(amount),
                    financialData.getSavings(),
                    financialData.getDate()
            );
            updateDatabase();
            return true;
        } else {
            return false;
        }
    }
    public boolean addToSavings(BigDecimal amount) {
        if (financialData.getBalance().compareTo(amount) >= 0) {
            financialData = new FinancialData(
                    financialData.getBalance().subtract(amount),
                    financialData.getSpentToday(),
                    financialData.getSavings().add(amount),
                    financialData.getDate()
            );
            updateDatabase();
            return true;
        } else {
            return false;
        }
    }
    public boolean withdrawFromSavings(BigDecimal amount) {
        if (financialData.getSavings().compareTo(amount) >= 0) {
            financialData = new FinancialData(
                    financialData.getBalance().add(amount),
                    financialData.getSpentToday(),
                    financialData.getSavings().subtract(amount),
                    financialData.getDate()
            );
            updateDatabase();
            return true;
        } else {
            return false;
        }
    }
    public BigDecimal getBalance() {
        return financialData.getBalance();
    }

    public BigDecimal getSavings() {
        return financialData.getSavings();
    }

    public BigDecimal getSpentToday() {
        return financialData.getSpentToday();
    }
}
