package com.example.bankapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView greeting_text;
    DatabaseHelper db;
    BankUser bankUser = null;
    ArrayList<SavingsAccount> savingsAccountArrayList;
    ArrayList<CurrentAccount> currentAccountArrayList;
    ArrayList<Transaction> transactionArrayList;
    ListView account_list, transaction_list;
    ArrayList<UserBankaccounts> user_bankaccounts;
    Context context = this;
    Button file_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        account_list = findViewById(R.id.account_list);
        transaction_list = findViewById(R.id.transaction_list);
        file_save = findViewById(R.id.file_button);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        bankUser = (BankUser) getIntent().getSerializableExtra("bank_user");

        greeting_text = findViewById(R.id.greeting_text);
        greeting_text.setText("Welcome " + bankUser.getUsername() +"!");

        db = new DatabaseHelper(this);

        savingsAccountArrayList = db.getSavingsAccounts(bankUser.getUsername());
        currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
        bankUser.setCurrentAccounts(currentAccountArrayList);
        bankUser.setSavingsAccounts(savingsAccountArrayList);


        user_bankaccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);


        if(user_bankaccounts.isEmpty() == false) {
            ArrayAdapter<UserBankaccounts> account_list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, user_bankaccounts);
            account_list.setAdapter(account_list_adapter);
        } else {
            ArrayList<String> noAccounts = new ArrayList<>();
            noAccounts.add("You have no bank accounts!");

            ArrayAdapter<String> account_list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noAccounts);
            account_list.setAdapter(account_list_adapter);
        }

        //Listener that allows clicking the account to show transactions of that account
        account_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int accountnum = user_bankaccounts.get(i).getAccountnumber();
                transactionArrayList = db.getTransactions(accountnum);
                for(SavingsAccount savingsAccount: savingsAccountArrayList){
                    if(accountnum == savingsAccount.getAccountnumber()){
                        savingsAccount.setTransactions(transactionArrayList);
                    }
                }

                for(CurrentAccount currentAccount: currentAccountArrayList){
                    if(accountnum == currentAccount.getAccountnumber()){
                        currentAccount.setTransactions(transactionArrayList);
                    }
                }

                ArrayList<String> transaction_data = new ArrayList<>();
                for (Transaction transaction : transactionArrayList){
                        transaction_data.add("Accountnumber: " + transaction.toString());
                }
                ArrayAdapter<String> transaction_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, transaction_data);
                transaction_list.setAdapter(transaction_adapter);

            }
        });

        //button to save in the JSON file
        file_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<UserBankaccounts> userBankaccounts;
                ArrayList<Transaction> transactionArrayList_all = new ArrayList<>();
                    userBankaccounts = createCombinedAccountList(savingsAccountArrayList, currentAccountArrayList);
                    for( UserBankaccounts bankaccounts : userBankaccounts){
                        transactionArrayList = db.getTransactions(bankaccounts.getAccountnumber());
                        for(Transaction transaction : transactionArrayList){
                            transactionArrayList_all.add(transaction);
                        }
                    }
                    CreateJSONFile CJF = new CreateJSONFile(transactionArrayList_all);
                    CJF.createFile(context.getFilesDir());
                }
        });

    }


    //Takes in two arraylists that have different accounts tied to an username, and combines them into one arraylist. Then
    //returns this arraylist with all accounts
    public ArrayList<UserBankaccounts> createCombinedAccountList(ArrayList<SavingsAccount> savingsAccountArrayList,ArrayList<CurrentAccount> currentAccountArrayList){
        ArrayList<UserBankaccounts> user_bankaccounts = new ArrayList<>();

        DecimalFormat decimalFormat = new DecimalFormat("##0.00");

        for(SavingsAccount savingsAccount : savingsAccountArrayList){
            String money = decimalFormat.format(savingsAccount.getMoney());

            String account_information = "\nAccount name: " + savingsAccount.getAccountname() + "\nAccount number: " + savingsAccount.getAccountnumber() + "\nMoney: " + money + "€\n";
            user_bankaccounts.add(new UserBankaccounts(account_information, savingsAccount.getAccountnumber()));
        }

        for(CurrentAccount currentAccount : currentAccountArrayList){
            String money = decimalFormat.format(currentAccount.getMoney());
            String paylimit_account = decimalFormat.format(currentAccount.getAccount_withdrawlimit());

            if(currentAccount.getCardnumber_credit() != -1) {
                String paylimit_credit = decimalFormat.format(currentAccount.getCredit_paylimit());
                String creditlimit = decimalFormat.format(currentAccount.getCreditlimit());
                String account_information = "\nAccount name: " + currentAccount.getAccountname() + "\nAccount number: " + currentAccount.getAccountnumber() + "\nMoney: " + money + "€\nPaylimit: " + paylimit_account + "€\nCardnumber: " + currentAccount.getCardnumber_credit() + "\nPaylimit: " + paylimit_credit + "€\nCreditlimit: " + creditlimit + "€\n";
                user_bankaccounts.add(new UserBankaccounts(account_information, currentAccount.getAccountnumber()));
            }
            else if(currentAccount.getCardnumber_debit() != -1){
                String paylimit_debit = decimalFormat.format(currentAccount.getDebit_paylimit());
                String account_information = "\nAccount name: " + currentAccount.getAccountname() + "\nAccount number: " + currentAccount.getAccountnumber() + "\nMoney: " + money + "€\nPaylimit: " + paylimit_account + "€\nCardnumber: " + currentAccount.getCardnumber_debit() + "\nPaylimit: " + paylimit_debit + "€\n";
                user_bankaccounts.add(new UserBankaccounts(account_information, currentAccount.getAccountnumber()));
            } else {
                String account_information = "\nAccount name: " + currentAccount.getAccountname() + "\nAccount number: " + currentAccount.getAccountnumber() + "\nMoney: " + money + "€\nPaylimit: " + paylimit_account + "€\n";
                user_bankaccounts.add(new UserBankaccounts(account_information, currentAccount.getAccountnumber()));
            }
        }
        return user_bankaccounts;
    }

    //modified backpress, that asks if the user wants to sign out through a fragment
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to sign out?");
            // Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("key","value");
                    startActivityForResult(intent, 10);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //sidebar navigation, same in all the major activities
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payments) {
            Intent intent = new Intent(MainActivity.this,PaymentsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 2);
            finish();
        } else if (id == R.id.nav_manage_user) {
            Intent intent = new Intent(MainActivity.this,ManageUser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 3);
            finish();
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(MainActivity.this, ManageAccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bank_user", bankUser);
            startActivityForResult(intent, 4);
            finish();
        } else if (id == R.id.nav_signout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to sign out?");
            // Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("key","value");
                    startActivityForResult(intent, 10);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
