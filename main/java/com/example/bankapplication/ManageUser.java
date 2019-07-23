package com.example.bankapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ManageUser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BankUser bankUser;
    TextView name_view, address_view, phonenumber_view, bankname_view;
    EditText name_edit, address_edit, phonenumber_edit;
    String bank_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        name_view = findViewById(R.id.name_view);
        address_view = findViewById(R.id.address_view);
        phonenumber_view = findViewById(R.id.phonenumber_view);
        bankname_view = findViewById(R.id.bankname_view);
        name_edit = findViewById(R.id.user_name_edit);
        address_edit = findViewById(R.id.user_address_edit);
        phonenumber_edit = findViewById(R.id.user_phonenumber_edit);

        DatabaseHelper db = new DatabaseHelper(this);


        bankUser = (BankUser) getIntent().getSerializableExtra("bank_user");
        ArrayList<Bank> bankArrayList = db.getBanks(bankUser.getUsername());
        for(Bank bank : bankArrayList){
            bank_name = bank.getName();
        }

        if(bankUser.getName() != null && !bankUser.getName().isEmpty()) {
            name_view.setText("Name: " + bankUser.getName());
        }
        if(bankUser.getAddress() != null && !bankUser.getAddress().isEmpty()){
            address_view.setText("Address: " + bankUser.getAddress());
        }
        if(bankUser.getPhonenumber() != null && !bankUser.getPhonenumber().isEmpty()) {
            phonenumber_view.setText("Phonenumber: " + bankUser.getPhonenumber());
        }
        if(bank_name != null && !bank_name.isEmpty()) {
            bankname_view.setText("Bank: " + bank_name);
        }


    }

    //update button to update the user both in class and database
    public void updateBankUser(View v){
        DatabaseHelper db = new DatabaseHelper(this);
        String new_name;
        String new_address;
        String new_phonenumber;
        String bank_username = bankUser.getUsername();

        new_name = name_edit.getText().toString();
        new_address = address_edit.getText().toString();
        new_phonenumber = phonenumber_edit.getText().toString();

        db.setBankUser(bank_username, new_name, new_phonenumber, new_address);

        ArrayList<BankUser> bankUserArrayList = db.getBankUsers();

        for(BankUser bankUserUpdated : bankUserArrayList) {
            System.out.println(bank_username);
            System.out.println(bankUserUpdated.getUsername());
            if (bankUserUpdated.getUsername().matches(bank_username)){
                bankUser = bankUserUpdated;
                address_view.setText("Address: " + bankUser.getAddress());
                phonenumber_view.setText("Phonenumber: " + bankUser.getPhonenumber());
                name_view.setText("Name: " + bankUser.getName());
                bankname_view.setText("Bank: " + bank_name);

                Toast update_success = Toast.makeText(this,"User updated successfully.", Toast.LENGTH_SHORT);
                update_success.show();
                return;
            }
        }
        Toast update_failure = Toast.makeText(this,"User update failed.", Toast.LENGTH_SHORT);
        update_failure.show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(ManageUser.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 1);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_user, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(ManageUser.this,MainActivity.class);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 1);
            finish();
        } else if (id == R.id.nav_payments) {
            Intent intent = new Intent(ManageUser.this,PaymentsActivity.class);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 2);
            finish();
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(ManageUser.this, ManageAccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bank_user", bankUser);
            startActivityForResult(intent, 4);
            finish();
        } else if (id == R.id.nav_signout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageUser.this);
            builder.setMessage("Are you sure you want to sign out?");
            // Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(ManageUser.this, LoginActivity.class);
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

