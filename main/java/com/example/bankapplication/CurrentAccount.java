package com.example.bankapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class CurrentAccount implements Serializable {
    private Integer accountnumber;
    private String username = "";
    private Float money;
    private String accountname = "";
    private Float account_paylimit;
    private Integer cardnumber_credit;
    private Integer cardnumber_debit;
    private Float creditlimit;
    private Float credit_paylimit;
    private Float debit_paylimit;
    private ArrayList<Transaction> transactions;

    CurrentAccount(Integer accountnumber, String username, Float money, String accountname, Float account_paylimit, Integer cardnumber_credit, Float creditlimit, Float credit_paylimit, Integer cardnumber_debit, Float debit_paylimit){
        this.accountnumber = accountnumber;
        this.username = username;
        this.money = money;
        this.accountname = accountname;
        this.account_paylimit = account_paylimit;
        this.cardnumber_credit = cardnumber_credit;
        this.creditlimit = creditlimit;
        this.credit_paylimit = credit_paylimit;
        this.cardnumber_debit = cardnumber_debit;
        this.debit_paylimit = debit_paylimit;
    }

    @Override
    public String toString() {
        return Integer.toString(accountnumber);
    }

    public Float getMoney() {
        return money;
    }

    public Integer getAccountnumber() {
        return accountnumber;
    }

    public Float getAccount_withdrawlimit() {
        return account_paylimit;
    }

    public String getAccountname() {
        return accountname;
    }

    public Integer getCardnumber_credit() {
        return cardnumber_credit;
    }

    public String getUsername() {
        return username;
    }

    public Float getCredit_paylimit() {
        return credit_paylimit;
    }

    public Float getCreditlimit() {
        return creditlimit;
    }

    public Float getDebit_paylimit() {
        return debit_paylimit;
    }

    public Integer getCardnumber_debit() {
        return cardnumber_debit;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
