package com.example.bankapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CreateJSONFile {
    private ArrayList<Transaction> transactions;

    CreateJSONFile(ArrayList<Transaction> transactions){
        this.transactions = transactions;
    }

    //method that creates the JSON file, takes in the directory where it's made
    public void createFile(File directory) {
        String file_name = "BankApplication_transactions.json";
        JSONObject obj = new JSONObject();
        String transaction_string = "";
        for (Transaction transaction : transactions) {
            try {
                obj.put("accountnumber", transaction.getAccountnumber());
                obj.put("amount", transaction.getAmount());
                transaction_string = transaction_string + obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            File file = new File(directory, file_name);
            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;
            if(!file.exists()) {
                try {
                    file.createNewFile();
                    fileWriter = new FileWriter(file.getAbsolutePath());
                    bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write("{}");
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                fileWriter = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write(obj.toString());
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
