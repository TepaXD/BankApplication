package com.example.bankapplication;

public class BankAdmin {
    private String username = "";
    private String password = "";
    private int bankid;

    BankAdmin(String username, String password, int bankid){
        this.username = username;
        this.password = password;
        this.bankid = bankid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
