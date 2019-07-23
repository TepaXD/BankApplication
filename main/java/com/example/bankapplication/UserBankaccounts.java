package com.example.bankapplication;

public class UserBankaccounts {
    private String info;
    private int accountnumber;

    UserBankaccounts(String info, int accountnumber){
        this.info = info;
        this.accountnumber = accountnumber;
    }

    @Override
    public String toString() {
        return info;
    }

    public int getAccountnumber() {
        return accountnumber;
    }
}
