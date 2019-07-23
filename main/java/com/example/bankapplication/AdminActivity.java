package com.example.bankapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseHelper db = new DatabaseHelper(this);
    ArrayList<SavingsAccount> savingsAccountArrayList;
    ArrayList<CurrentAccount> currentAccountArrayList;
    ArrayList<Transaction> transactionArrayList;
    ListView account_list, transaction_list;
    ArrayList<UserBankaccounts> user_bankaccounts;
    Context context = this;
    Spinner user_spinner;
    ArrayList<BankUser> bankUserArrayList;
    TextView nameview, addressview, phonenumview, bankview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        account_list = findViewById(R.id.account_list);
        transaction_list = findViewById(R.id.transaction_list);
        user_spinner = findViewById(R.id.user_spinner);
        bankUserArrayList = new ArrayList<>();
        bankUserArrayList = db.getBankUsers();
        nameview = findViewById(R.id.nameview);
        addressview = findViewById(R.id.addressview);
        phonenumview = findViewById(R.id.phonenumview);
        bankview = findViewById(R.id.bankview);

        ArrayList<String> spinner_array = new ArrayList<>();
        spinner_array.add("Select user");
        for (BankUser bankUser : bankUserArrayList){
            spinner_array.add(bankUser.getUsername());
        }

        ArrayAdapter<String> user_spinner_adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, spinner_array);
        user_spinner.setAdapter(user_spinner_adapter);

        user_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int arrayid = user_spinner.getSelectedItemPosition();
                if(arrayid == 0){
                    return;
                }else {
                    String username = bankUserArrayList.get(arrayid-1).getUsername();
                    if(bankUserArrayList.get(arrayid-1).getName() != null && !bankUserArrayList.get(arrayid-1).getName().isEmpty()){
                        nameview.setText(bankUserArrayList.get(arrayid-1).getName());
                    }
                    if(bankUserArrayList.get(arrayid-1).getPhonenumber() != null && !bankUserArrayList.get(arrayid-1).getPhonenumber().isEmpty()){
                        phonenumview.setText(bankUserArrayList.get(arrayid-1).getPhonenumber());
                    }
                    if(bankUserArrayList.get(arrayid-1).getAddress() != null && !bankUserArrayList.get(arrayid-1).getAddress().isEmpty()){
                       addressview.setText(bankUserArrayList.get(arrayid-1).getAddress());
                    }
                    ArrayList<Bank> bank_name;
                    bank_name = db.getBanks(username);
                    bankview.setText(bank_name.get(0).getName());
                    savingsAccountArrayList = db.getSavingsAccounts(username);
                    currentAccountArrayList = db.getCurrentAccounts(username);

                    user_bankaccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);
                    if(user_bankaccounts.isEmpty() == false) {
                        ArrayAdapter<UserBankaccounts> account_list_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, user_bankaccounts);
                        account_list.setAdapter(account_list_adapter);
                    } else {
                        ArrayList<String> noAccounts = new ArrayList<>();
                        noAccounts.add("You have no bank accounts!");

                        ArrayAdapter<String> account_list_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, noAccounts);
                        account_list.setAdapter(account_list_adapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        account_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (user_bankaccounts.get(i).getAccountnumber() != 0) {

                    int accountnum = user_bankaccounts.get(i).getAccountnumber();
                    transactionArrayList = db.getTransactions(accountnum);
                    for (SavingsAccount savingsAccount : savingsAccountArrayList) {
                        if (accountnum == savingsAccount.getAccountnumber()) {
                            savingsAccount.setTransactions(transactionArrayList);
                        }
                    }

                    for (CurrentAccount currentAccount : currentAccountArrayList) {
                        if (accountnum == currentAccount.getAccountnumber()) {
                            currentAccount.setTransactions(transactionArrayList);
                        }
                    }

                    ArrayList<String> transaction_data = new ArrayList<>();
                    for (Transaction transaction : transactionArrayList) {
                        transaction_data.add("Accountnumber: " + transaction.toString());
                    }
                    ArrayAdapter<String> transaction_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, transaction_data);
                    transaction_list.setAdapter(transaction_adapter);

                } else {
                    return;
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setMessage("Are you sure you want to sign out?");
                // Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("key", "value");
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
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.nav_gallery) {
             Intent intent = new Intent(AdminActivity.this, AdminDelete.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             intent.putExtra("key", "value");
             startActivityForResult(intent, 10);
             finish();
        } else if (id == R.id.nav_slideshow) {
             Intent intent = new Intent(AdminActivity.this, AdminAdd.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             intent.putExtra("key", "value");
             startActivityForResult(intent, 10);
             finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
