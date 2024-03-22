package com.example.corinlicense;

import java.util.Date;

public class FinancialTransaction {
    private int id;
    private String balanceCount;
    private String spentTodayCount;
    private String savingsCount;
    private Date date;


    public FinancialTransaction(int id, String balanceCount, String spentTodayCount, String savingsCount, Date date) {
        this.id = id;
        this.balanceCount = balanceCount;
        this.spentTodayCount = spentTodayCount;
        this.savingsCount = savingsCount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBalanceCount() {
        return balanceCount;
    }

    public void setBalanceCount(String balanceCount) {
        this.balanceCount = balanceCount;
    }

    public String getSpentTodayCount() {
        return spentTodayCount;
    }

    public void setSpentTodayCount(String spentTodayCount) {
        this.spentTodayCount = spentTodayCount;
    }

    public String getSavingsCount() {
        return savingsCount;
    }

    public void setSavingsCount(String savingsCount) {
        this.savingsCount = savingsCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}