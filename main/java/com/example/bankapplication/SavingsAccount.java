package com.example.bankapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class SavingsAccount implements Serializable {
    private int accountnumber;
    private String username = "";
    private float money;
    private String accountname = "";
    private ArrayList<Transaction> transactions;

    SavingsAccount(int accountnumber, String username, float money, String accountname){
        this.accountnumber = accountnumber;
        this.username = username;
        this.money = money;
        this.accountname = accountname;
    }

    @Override
    public String toString() {
        return accountnumber + ", " + money + ", " + accountname;
    }

    public int getAccountnumber() {
        return accountnumber;
    }

    public float getMoney() {
        return money;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
