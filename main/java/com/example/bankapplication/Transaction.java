package com.example.bankapplication;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Transaction implements Serializable {
    private int accountnumber;
    private Float amount;

    Transaction(int accountnumber, Float amount){
        this.accountnumber = accountnumber;
        this.amount = amount;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String transaction = decimalFormat.format(amount);
        return accountnumber + "\tAmount: " + transaction + "€";
    }

    public int getAccountnumber() {
        return accountnumber;
    }

    public Float getAmount() {
        return amount;
    }
}


