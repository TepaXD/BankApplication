package com.example.bankapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

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
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdminDelete extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Spinner user_delete_spinner, account_delete_spinner;
    Button user_delete_button, account_delete_button, card_delete_button;
    ArrayList<BankUser> bankUserArrayList;
    ArrayList<SavingsAccount> savingsAccountArrayList;
    ArrayList<CurrentAccount> currentAccountArrayList;
    ArrayList<UserBankaccounts> user_bankaccounts;
    DatabaseHelper db;
    Context context = this;
    String username;
    int accountnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        db = new DatabaseHelper(this);
        bankUserArrayList = new ArrayList<>();
        bankUserArrayList = db.getBankUsers();
        user_delete_spinner = findViewById(R.id.user_delete_spinner);
        account_delete_spinner = findViewById(R.id.account_delete_spinner);
        user_delete_button = findViewById(R.id.user_delete_button);
        account_delete_button = findViewById(R.id.account_delete_button);
        card_delete_button = findViewById(R.id.delete_card_button);

        ArrayList<String> spinner_array = new ArrayList<>();
        spinner_array.add("Select user");
        for (BankUser bankUser : bankUserArrayList){
            spinner_array.add(bankUser.getUsername());
        }
        ArrayAdapter<String> user_spinner_adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, spinner_array);
        user_delete_spinner.setAdapter(user_spinner_adapter);

        user_delete_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int arrayid = user_delete_spinner.getSelectedItemPosition();
                if(arrayid == 0){
                    ArrayList<String> noAccounts = new ArrayList<>();
                    noAccounts.add("Select an account");

                    ArrayAdapter<String> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, noAccounts);
                    account_delete_spinner.setAdapter(account_spinner_adapter);
                    return;
                }else {
                    username = bankUserArrayList.get(arrayid-1).getUsername();
                    savingsAccountArrayList = db.getSavingsAccounts(username);
                    currentAccountArrayList = db.getCurrentAccounts(username);
                    user_bankaccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);
                    if(user_bankaccounts.isEmpty() == false) {
                        ArrayAdapter<UserBankaccounts> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, user_bankaccounts);
                        account_delete_spinner.setAdapter(account_spinner_adapter);
                    } else {
                        ArrayList<String> noAccounts = new ArrayList<>();
                        noAccounts.add("You have no bank accounts!");

                        ArrayAdapter<String> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, noAccounts);
                        account_delete_spinner.setAdapter(account_spinner_adapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        card_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAccountArrayList = db.getCurrentAccounts(username);
                for(CurrentAccount currentAccount : currentAccountArrayList){
                    if(accountnumber == currentAccount.getAccountnumber()){
                        if (currentAccount.getCardnumber_credit() > -1){
                            int cardnumber = currentAccount.getCardnumber_credit();
                            db.deleteCard(accountnumber,cardnumber);
                            user_delete_spinner.setSelection(0);
                            account_delete_spinner.setSelection(0);
                            if(user_bankaccounts.isEmpty() == false) {
                                ArrayAdapter<UserBankaccounts> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, user_bankaccounts);
                                account_delete_spinner.setAdapter(account_spinner_adapter);
                            } else {
                                ArrayList<String> noAccounts = new ArrayList<>();
                                noAccounts.add("You have no bank accounts!");

                                ArrayAdapter<String> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, noAccounts);
                                account_delete_spinner.setAdapter(account_spinner_adapter);
                            }
                        }
                        else if(currentAccount.getCardnumber_debit() > -1){
                            int cardnumber = currentAccount.getCardnumber_debit();
                            db.deleteCard(accountnumber,cardnumber);
                            user_delete_spinner.setSelection(0);
                            account_delete_spinner.setSelection(0);
                            if(user_bankaccounts.isEmpty() == false) {
                                ArrayAdapter<UserBankaccounts> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, user_bankaccounts);
                                account_delete_spinner.setAdapter(account_spinner_adapter);
                            } else {
                                ArrayList<String> noAccounts = new ArrayList<>();
                                noAccounts.add("You have no bank accounts!");

                                ArrayAdapter<String> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, noAccounts);
                                account_delete_spinner.setAdapter(account_spinner_adapter);
                            }
                        } else {
                            Toast fail_card = Toast.makeText(context,"Account doesn't have a card", Toast.LENGTH_SHORT);
                            fail_card.show();
                            return;
                        }
                    }
                }
            }
        });

        account_delete_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int arrayid = account_delete_spinner.getSelectedItemPosition();
                if (arrayid == 0) {
                    return;
                }

                accountnumber = user_bankaccounts.get(arrayid).getAccountnumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        user_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteUser(username);
                Toast user_deleted = Toast.makeText(context,"User deleted successfully",Toast.LENGTH_SHORT);
                user_deleted.show();

                ArrayList<String> spinner_array = new ArrayList<>();
                spinner_array.add("Select user");
                for (BankUser bankUser : bankUserArrayList){
                    spinner_array.add(bankUser.getUsername());
                }
                ArrayAdapter<String> user_spinner_adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item, spinner_array);
                user_delete_spinner.setAdapter(user_spinner_adapter);
                user_delete_spinner.setSelection(0);
                savingsAccountArrayList = db.getSavingsAccounts(username);
                currentAccountArrayList = db.getCurrentAccounts(username);
                user_bankaccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);
                user_spinner_adapter.notifyDataSetChanged();
            }
        });

        account_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteAccount(accountnumber);
                if(user_bankaccounts.isEmpty() == false) {
                    ArrayAdapter<UserBankaccounts> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, user_bankaccounts);
                    account_delete_spinner.setAdapter(account_spinner_adapter);
                } else {
                    ArrayList<String> noAccounts = new ArrayList<>();
                    noAccounts.add("You have no bank accounts!");

                    ArrayAdapter<String> account_spinner_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, noAccounts);
                    account_delete_spinner.setAdapter(account_spinner_adapter);
                }

                account_delete_spinner.setSelection(0);
                user_delete_spinner.setSelection(0);
                Toast account_deleted = Toast.makeText(context,"Account deleted successfully",Toast.LENGTH_SHORT);
                account_deleted.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(AdminDelete.this, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key", "value");
            startActivityForResult(intent, 10);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_delete, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(AdminDelete.this, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key", "value");
            startActivityForResult(intent, 10);
            finish();
        }else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(AdminDelete.this, AdminAdd.class);
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


        for(SavingsAccount savingsAccount : savingsAccountArrayList){

            String account_information = "Account number: " + savingsAccount.getAccountnumber();
            user_bankaccounts.add(new UserBankaccounts(account_information, savingsAccount.getAccountnumber()));
        }

        for(CurrentAccount currentAccount : currentAccountArrayList){
                String account_information = "Account number: " + currentAccount.getAccountnumber();
                user_bankaccounts.add(new UserBankaccounts(account_information, currentAccount.getAccountnumber()));
        }
        return user_bankaccounts;
    }
}
