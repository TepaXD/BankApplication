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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminAdd extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context context = this;
    EditText add_accountname, add_money, add_accountpaylimit, add_cardpaylimit, add_cardcreditlimit, add_username, add_password;
    Switch add_newAccount_card;
    RadioGroup card_radioGroup, account_radioGroup;
    RadioButton newSavingAccount, newCurrentAccount, newAccount_debit_card, newAccount_credit_card;
    Button addbutton, adduser;
    DatabaseHelper db;
    String accountnumber;
    BankUser bankUser;
    Spinner user_spinner;
    ArrayList<BankUser> bankUserArrayList;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);
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

        add_accountname = findViewById(R.id.add_accountname);
        add_money = findViewById(R.id.add_money);
        add_accountpaylimit = findViewById(R.id.add_paylimit);
        add_cardcreditlimit = findViewById(R.id.add_creditlimit);
        add_cardpaylimit = findViewById(R.id.add_card_paylimit);
        add_newAccount_card = findViewById(R.id.add_newAccount_card);
        card_radioGroup = findViewById(R.id.card_radioGroup);
        account_radioGroup = findViewById(R.id.account_radioGroup);
        newAccount_credit_card = findViewById(R.id.radioButton_addCredit);
        newAccount_debit_card = findViewById(R.id.radioButton_addDebit);
        newSavingAccount = findViewById(R.id.radioButton_saving);
        newCurrentAccount = findViewById(R.id.radiobutton_current);
        addbutton = findViewById(R.id.add_button);
        adduser = findViewById(R.id.user_add_button);
        add_username = findViewById(R.id.give_username);
        add_password = findViewById(R.id.give_password);
        user_spinner = findViewById(R.id.user_spinner);

        ArrayList<String> spinner_array = new ArrayList<>();
                spinner_array.add("Select user");
                for (BankUser bankUser : bankUserArrayList){
                    spinner_array.add(bankUser.getUsername());
                }
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accountname = null;
                String username = bankUser.getUsername();
                String paylimit = null;
                String money = null;
                String cardpaylimit = null;
                String cardcreditlimit = null;

                if(add_accountname.getText().toString() != null) {
                    accountname = add_accountname.getText().toString();
                }
                if(add_accountpaylimit.getText().toString() != null){
                    paylimit = add_accountpaylimit.getText().toString();
                }
                if(add_money.getText().toString() != null){
                    money = add_money.getText().toString();
                }
                if(add_cardpaylimit.getText().toString() != null) {
                    cardpaylimit = add_cardpaylimit.getText().toString();
                }
                if(add_cardcreditlimit.getText().toString() != null) {
                    cardcreditlimit = add_cardcreditlimit.getText().toString();
                }

                accountnumber = db.generateAccountNumber();

                if(newSavingAccount.isChecked() == true){
                    boolean state_saving = db.addSavingsAccount(username,money,accountnumber,accountname);
                    if (state_saving == true) {
                        Toast toast_success = Toast.makeText(context, "Savings account created successfully.", Toast.LENGTH_SHORT);
                        toast_success.show();
                    } else {
                        Toast toast_failure = Toast.makeText(context, "Error in creating savings account.", Toast.LENGTH_SHORT);
                        toast_failure.show();
                    }

                } else if(newCurrentAccount.isChecked() == true){
                    boolean state_current = db.addCurrentAccount(username,money,accountnumber,accountname,paylimit);
                    if(newAccount_debit_card.isChecked() == true){
                        boolean state_debit = db.addDebitCard(cardpaylimit,accountnumber);
                        if (state_debit == true && state_current == true) {
                            Toast toast_success = Toast.makeText(context, "Current account created successfully with a debit card.", Toast.LENGTH_SHORT);
                            toast_success.show();
                        } else {
                            Toast toast_failure = Toast.makeText(context, "Error in creating a current account with debit card.", Toast.LENGTH_SHORT);
                            toast_failure.show();
                        }
                    } else if(newAccount_credit_card.isChecked() == true){
                        boolean state_credit = db.addCreditCard(cardpaylimit,cardcreditlimit,accountnumber);
                        if (state_credit == true && state_current == true) {
                            Toast toast_success = Toast.makeText(context, "Current account created successfully with a credit card.", Toast.LENGTH_SHORT);
                            toast_success.show();
                        } else {
                            Toast toast_failure = Toast.makeText(context, "Error in creating a current account with credit card.", Toast.LENGTH_SHORT);
                            toast_failure.show();
                        }
                    } else {
                        if (state_current == true) {
                            Toast toast_success = Toast.makeText(context, "Current account created successfully.", Toast.LENGTH_SHORT);
                            toast_success.show();
                        } else {
                            Toast toast_failure = Toast.makeText(context, "Error in creating a current account.", Toast.LENGTH_SHORT);
                            toast_failure.show();
                        }
                    }

                }

            }
        });

              adduser.setOnClickListener(new View.OnClickListener(){
                  @Override
                  public void onClick(View view) {
                      String username = add_username.getText().toString();
                      String password = add_password.getText().toString();
                      Context context = getApplicationContext();
                      if(username.equals("")) {
                          Toast toast_username_warning = Toast.makeText(context,"Please give an username first.",Toast.LENGTH_SHORT);
                          toast_username_warning.show();
                      }else if(password.equals("")){
                          Toast toast_password_warning = Toast.makeText(context,"Please give a password first.",Toast.LENGTH_SHORT);
                          toast_password_warning.show();
                      }else {
                          boolean state = db.addBankUser(username, password);
                          if (state == true) {
                              Toast toast_success = Toast.makeText(context, "User created successfully.", Toast.LENGTH_SHORT);
                              toast_success.show();
                          } else {
                              Toast toast_failure = Toast.makeText(context, "Error in registering a user.", Toast.LENGTH_SHORT);
                              toast_failure.show();
                          }
                      }
                  }
              });


        


        add_newAccount_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(add_newAccount_card.isChecked() == false){
                    newAccount_credit_card.setClickable(false);
                    newAccount_debit_card.setClickable(false);
                    newAccount_credit_card.setChecked(false);
                    newAccount_debit_card.setChecked(false);
                    add_cardpaylimit.setText(null);
                    add_cardpaylimit.setEnabled(false);
                    add_cardcreditlimit.setText(null);
                    add_cardcreditlimit.setEnabled(false);
                } else {
                    newAccount_credit_card.setClickable(true);
                    newAccount_debit_card.setClickable(true);
                    add_cardcreditlimit.setEnabled(true);
                    add_cardpaylimit.setEnabled(true);
                }
            }
        });

         user_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 int arrayid = user_spinner.getSelectedItemPosition();
                 if (arrayid == 0) {
                     return;
                 }
             }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
                 });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_add, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(AdminAdd.this, AdminActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key", "value");
            startActivityForResult(intent, 10);
            finish();

        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(AdminAdd.this, AdminDelete.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key", "value");
            startActivityForResult(intent, 10);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}





















