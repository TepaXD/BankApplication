package com.example.bankapplication;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class BankUser implements Serializable {
    private String username = "";
    private String password = "";
    private String name = "";
    private String address = "";
    private String phonenumber = "";
    private int bankid;
    private ArrayList<SavingsAccount> savingsAccounts;
    private ArrayList<CurrentAccount> currentAccounts;

    BankUser(String username, String password, String name, String address, String phonenumber, int bankid){
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phonenumber = phonenumber;
        this.bankid = bankid;
    }

    public void setCurrentAccounts(ArrayList<CurrentAccount> currentAccounts) {
        this.currentAccounts = currentAccounts;
    }

    public void setSavingsAccounts(ArrayList<SavingsAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }

    @Override
    public String toString() {
        return username + ", " + password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getBankid() {
        return bankid;
    }

    public String getAddress() {
        return address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
}

