package com.example.bankapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BankUser bankUser = null;
    Spinner account_spinner, spinner_transfer;
    TextView account_withdrawlimit, card_type, card_paylimit, card_creditlimit;
    EditText money_amount;
    Button payment_button, withdraw_button, transfer_button;
    RadioButton account_select, card_select;
    DatabaseHelper db;
    ArrayList<CurrentAccount> currentAccountArrayList;
    ArrayList<SavingsAccount> savingsAccountArrayList;
    ArrayList<String> spinnerArrayList = new ArrayList<>();
    ArrayList<String> combined_accounts;
    ArrayList<Integer> accountnumbers = new ArrayList<>();
    ArrayList<Integer> combined_accountnumbers = new ArrayList<>();
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        account_spinner = findViewById(R.id.account_spinner_payments);
        account_withdrawlimit = findViewById(R.id.account_withdrawlimit);
        card_type = findViewById(R.id.card_type);
        card_paylimit = findViewById(R.id.card_paylimit);
        card_creditlimit = findViewById(R.id.card_creditlimit);
        payment_button = findViewById(R.id.payment_button);
        money_amount = findViewById(R.id.money_amount);
        transfer_button = findViewById(R.id.transfer_button);
        withdraw_button = findViewById(R.id.withdraw_button);
        spinner_transfer = findViewById(R.id.spinner_transfer);

        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        bankUser = (BankUser) getIntent().getSerializableExtra("bank_user");
        db = new DatabaseHelper(this);

        currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
        savingsAccountArrayList = db.getSavingsAccounts(bankUser.getUsername());
        bankUser.setCurrentAccounts(currentAccountArrayList);
        bankUser.setSavingsAccounts(savingsAccountArrayList);
        spinnerArrayList.add("Account to pay/transfer/withdraw from");
        for(CurrentAccount currentAccount : currentAccountArrayList){
            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            String money = decimalFormat.format(currentAccount.getMoney());
            int accountnumber = currentAccount.getAccountnumber();
            spinnerArrayList.add("Account: " + accountnumber + " / Money: " + money + "€");
            accountnumbers.add(accountnumber);
        }

        combined_accounts = createCombinedAccountList(savingsAccountArrayList, currentAccountArrayList);
        ArrayAdapter<String> transfer_spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, combined_accounts);
        spinner_transfer.setAdapter(transfer_spinnerAdapter);



        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArrayList);
        account_spinner.setAdapter(spinnerAdapter);

        //spinner listener, that sets the values of the text fields
        account_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int accountnumber = 0;
                int spinnerid = account_spinner.getSelectedItemPosition();
                if(spinnerid != 0) {
                    accountnumber = accountnumbers.get(spinnerid - 1);
                }
                for(CurrentAccount currentAccount : currentAccountArrayList){
                    if(currentAccount.getAccountnumber() == accountnumber){
                        DecimalFormat decimalFormat = new DecimalFormat("##0.00");

                        account_withdrawlimit.setText(Float.toString(currentAccount.getAccount_withdrawlimit()));
                        if(currentAccount.getCardnumber_debit() != -1) {
                            account_withdrawlimit.setText("Account paylimit: " + decimalFormat.format(currentAccount.getAccount_withdrawlimit())+ "€");
                            card_paylimit.setText("Card paylimit: " + decimalFormat.format(currentAccount.getDebit_paylimit())+ "€");
                            card_type.setText("Card type: Debit");
                            card_creditlimit.setText("No credit limit on debit card.");
                        } else if (currentAccount.getCardnumber_credit() != -1){
                            account_withdrawlimit.setText("Account paylimit: " + decimalFormat.format(currentAccount.getAccount_withdrawlimit()) + "€");
                            card_paylimit.setText("Card paylimit: " + decimalFormat.format(currentAccount.getCredit_paylimit()) + "€");
                            card_type.setText("Card type: Credit");
                            card_creditlimit.setText("Creditlimit: " + decimalFormat.format(currentAccount.getCreditlimit()) + "€");
                        } else {
                            account_withdrawlimit.setText("Account paylimit: " + decimalFormat.format(currentAccount.getAccount_withdrawlimit())+ "€");
                            card_paylimit.setText("No card on account.");
                            card_type.setText("No card on account.");
                            card_creditlimit.setText("No card on account");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //payment transaction listener, bunch of conditions making sure the payment is valid
        payment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float money;
                if(money_amount.getText().toString() != null && !money_amount.getText().toString().isEmpty()) {
                    money = Float.parseFloat(money_amount.getText().toString());
                } else {
                    Toast no_money = Toast.makeText(context,"Please enter money",Toast.LENGTH_SHORT);
                    no_money.show();
                    return;
                }
                if(account_spinner.getSelectedItemPosition() == 0){
                    Toast choose_account = Toast.makeText(context, "Choose an account first", Toast.LENGTH_SHORT);
                    choose_account.show();
                    return;
                }
                    int spinnerid = account_spinner.getSelectedItemPosition() - 1;
                    int accountnumber = accountnumbers.get(spinnerid);
                    for (CurrentAccount currentAccount : currentAccountArrayList) {
                        if (accountnumber == currentAccount.getAccountnumber()) {
                            float accountmoney = currentAccount.getMoney();
                            if (currentAccount.getCardnumber_credit() != -1) {
                                    if ((currentAccount.getCredit_paylimit() >= money) || (currentAccount.getCredit_paylimit() == 0)) {
                                        if ((currentAccount.getCreditlimit() != 0) && (currentAccount.getCreditlimit() + accountmoney >= money)) {
                                            float new_accountmoney = accountmoney - money;
                                            db.paymentAccount(new_accountmoney, accountnumber);
                                            currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                            currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                        } else if (currentAccount.getCreditlimit() == 0 && currentAccount.getMoney() >= money) {
                                            float new_accountmoney = accountmoney - money;
                                            db.paymentAccount(new_accountmoney, accountnumber);
                                            currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                            currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                        } else {
                                            Toast money_failure = Toast.makeText(context, "Insufficient funds for a payment", Toast.LENGTH_SHORT);
                                            money_failure.show();
                                            return;
                                        }
                                    }
                            } else if (currentAccount.getCardnumber_debit() != -1) {
                                    if ((currentAccount.getDebit_paylimit() >= money) || (currentAccount.getDebit_paylimit() == 0) && (accountmoney >= money)) {
                                        float new_accountmoney = accountmoney - money;
                                        db.paymentAccount(new_accountmoney, accountnumber);
                                        currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                        currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                        money_amount.setText(null);
                                        Toast money_success = Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT);
                                        money_success.show();
                                    } else if (currentAccount.getCreditlimit() == 0 && currentAccount.getMoney() >= money) {
                                        float new_accountmoney = accountmoney - money;
                                        db.paymentAccount(new_accountmoney, accountnumber);
                                        currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                        currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                        money_amount.setText(null);
                                        Toast money_success = Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT);
                                        money_success.show();
                                    } else {
                                        Toast money_failure = Toast.makeText(context, "Insufficient funds for a payment", Toast.LENGTH_SHORT);
                                        money_failure.show();
                                        return;
                                    }

                            } else {
                                Toast money_failure = Toast.makeText(context, "Insufficient funds for a payment", Toast.LENGTH_SHORT);
                                money_failure.show();
                                return;
                            }
                        }
                    }
                spinnerArrayList.clear();
                db.addTransaction(accountnumber, -money);
                spinnerArrayList.add("Account to pay/transfer/withdraw from");
                for(CurrentAccount currentAccount: currentAccountArrayList){
                    DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                    String money_account = decimalFormat.format(currentAccount.getMoney());
                    int accountnumber_spinner = currentAccount.getAccountnumber();

                    spinnerArrayList.add("Account: " + accountnumber_spinner + " / Money: " + money_account + "€");
                    accountnumbers.add(accountnumber_spinner);
                    account_spinner.setSelection(0);
                    spinner_transfer.setSelection(0);
                    account_withdrawlimit.setText(null);
                    card_paylimit.setText(null);
                    card_type.setText(null);
                    card_creditlimit.setText(null);
                    Toast money_success = Toast.makeText(context,"Payment successful", Toast.LENGTH_SHORT);
                    money_success.show();
                    money_amount.setText(null);
                }
            }
        });

        //transfer transaction listener
        transfer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Float money;
                if (money_amount.getText().toString() != null && !money_amount.getText().toString().isEmpty()) {
                    money = Float.parseFloat(money_amount.getText().toString());
                } else {
                    Toast no_money = Toast.makeText(context, "Please enter the amount first", Toast.LENGTH_SHORT);
                    no_money.show();
                    return;
                }
                if (account_spinner.getSelectedItemPosition() == 0) {
                    Toast choose_account = Toast.makeText(context, "Choose an account first", Toast.LENGTH_SHORT);
                    choose_account.show();
                    return;
                }
                if (spinner_transfer.getSelectedItemPosition() == 0) {
                    Toast choose_account = Toast.makeText(context, "Choose an account to receive money", Toast.LENGTH_SHORT);
                    choose_account.show();
                    return;
                }

                int spinnerid_from = account_spinner.getSelectedItemPosition() - 1;
                int accountnumber_from = accountnumbers.get(spinnerid_from);
                int spinnerid_to = spinner_transfer.getSelectedItemPosition() - 1;
                int accountnumber_to = combined_accountnumbers.get(spinnerid_to);
                float accountmoney_to = 0;
                if(accountnumber_from == accountnumber_to){
                    Toast same_account = Toast.makeText(context, "Select different accounts", Toast.LENGTH_SHORT);
                    same_account.show();
                    return;
                }

                for (SavingsAccount savingsAccount : savingsAccountArrayList) {
                    if (accountnumber_to == savingsAccount.getAccountnumber()) {
                        accountmoney_to = savingsAccount.getMoney();
                    }
                    for (CurrentAccount currentAccount : currentAccountArrayList) {
                        if (accountnumber_to == currentAccount.getAccountnumber()) {
                            accountmoney_to = currentAccount.getMoney();
                        }
                        if (accountnumber_from == currentAccount.getAccountnumber()) {
                            float accountmoney_from = currentAccount.getMoney();
                            if (((currentAccount.getAccount_withdrawlimit() > money) || (currentAccount.getAccount_withdrawlimit() == 0)) && (accountmoney_from > money)) {
                                float new_accountmoney_from = accountmoney_from - money;
                                float new_accountmoney_to = accountmoney_to + money;
                                db.paymentAccount(new_accountmoney_from, accountnumber_from);
                                db.paymentAccount(new_accountmoney_to, accountnumber_to);
                                db.addTransaction(accountnumber_from, -money);
                                db.addTransaction(accountnumber_to, money);
                                currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                                money_amount.setText(null);
                                Toast transfer_success = Toast.makeText(context, "Transfer successful", Toast.LENGTH_SHORT);
                                transfer_success.show();

                            } else {
                                Toast money_failure = Toast.makeText(context, "Insufficient funds to transfer", Toast.LENGTH_SHORT);
                                money_failure.show();
                                return;
                            }

                        }
                    }
                }
                spinnerArrayList.clear();
                spinnerArrayList.add("Account to pay/transfer/withdraw from");
                for(CurrentAccount currentAccount: currentAccountArrayList){
                    DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                    String money_account = decimalFormat.format(currentAccount.getMoney());
                    int accountnumber_spinner = currentAccount.getAccountnumber();

                    spinnerArrayList.add("Account: " + accountnumber_spinner + " / Money: " + money_account + "€");
                    accountnumbers.add(accountnumber_spinner);
                    account_spinner.setSelection(0);
                    spinner_transfer.setSelection(0);
                    account_withdrawlimit.setText(null);
                    card_paylimit.setText(null);
                    card_type.setText(null);
                    card_creditlimit.setText(null);
                    Toast money_success = Toast.makeText(context,"Payment successful", Toast.LENGTH_SHORT);
                    money_success.show();
                    money_amount.setText(null);
                }
            }
        });

        //withdraw transaction listener
        withdraw_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Float money;
                if (money_amount.getText().toString() != null && !money_amount.getText().toString().isEmpty()) {
                    money = Float.parseFloat(money_amount.getText().toString());
                } else {
                    Toast no_money = Toast.makeText(context, "Please enter the amount first", Toast.LENGTH_SHORT);
                    no_money.show();
                    return;
                }
                if (account_spinner.getSelectedItemPosition() == 0) {
                    Toast choose_account = Toast.makeText(context, "Choose an account first", Toast.LENGTH_SHORT);
                    choose_account.show();
                    return;
                }

                int spinnerid = account_spinner.getSelectedItemPosition() - 1;
                int accountnumber = accountnumbers.get(spinnerid);
                for (CurrentAccount currentAccount : currentAccountArrayList) {
                    if (accountnumber == currentAccount.getAccountnumber()) {
                        float accountmoney = currentAccount.getMoney();
                        if (((currentAccount.getAccount_withdrawlimit() > money) || (currentAccount.getAccount_withdrawlimit() == 0)) && (accountmoney > money)) {
                            float new_accountmoney = accountmoney - money;
                            db.paymentAccount(new_accountmoney, accountnumber);
                            currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());
                            currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());

                        } else {
                            Toast money_failure = Toast.makeText(context, "Insufficient funds to withdraw", Toast.LENGTH_SHORT);
                            money_failure.show();
                            return;
                        }

                    }
                }
                spinnerArrayList.clear();
                spinnerArrayList.add("Account to pay/transfer/withdraw from");
                for (CurrentAccount currentAccount : currentAccountArrayList) {
                    DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                    String money_account = decimalFormat.format(currentAccount.getMoney());
                    int accountnumber_account = currentAccount.getAccountnumber();

                    spinnerArrayList.add("Account: " + accountnumber_account + " / Money: " + money_account + "€");
                    accountnumbers.add(accountnumber);
                    account_spinner.setSelection(0);
                    db.addTransaction(accountnumber, -money);
                    Toast money_success = Toast.makeText(context, "Withdrawal successful", Toast.LENGTH_SHORT);
                    money_success.show();
                    money_amount.setText(null);
                }
                spinnerArrayList.clear();
                spinnerArrayList.add("Account to pay/transfer/withdraw from");
                for(CurrentAccount currentAccount: currentAccountArrayList){
                    DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                    String money_account = decimalFormat.format(currentAccount.getMoney());
                    int accountnumber_spinner = currentAccount.getAccountnumber();

                    spinnerArrayList.add("Account: " + accountnumber_spinner + " / Money: " + money_account + "€");
                    accountnumbers.add(accountnumber_spinner);
                    account_spinner.setSelection(0);
                    spinner_transfer.setSelection(0);
                    account_withdrawlimit.setText(null);
                    card_paylimit.setText(null);
                    card_type.setText(null);
                    card_creditlimit.setText(null);
                    Toast money_success = Toast.makeText(context,"Payment successful", Toast.LENGTH_SHORT);
                    money_success.show();
                    money_amount.setText(null);
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
            Intent intent = new Intent(PaymentsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 10);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payments, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(PaymentsActivity.this,MainActivity.class);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 1);
            finish();
        } else if (id == R.id.nav_manage_user) {
            Intent intent = new Intent(PaymentsActivity.this,ManageUser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 3);
            finish();
        } else if (id == R.id.nav_manage_account) {
            Intent intent = new Intent(PaymentsActivity.this, ManageAccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bank_user", bankUser);
            startActivityForResult(intent, 4);
            finish();
        } else if (id == R.id.nav_signout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentsActivity.this);
            builder.setMessage("Are you sure you want to sign out?");
            // Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(PaymentsActivity.this, LoginActivity.class);
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

    //adds all accounts from both tables into one big arraylist
    public ArrayList<String> createCombinedAccountList(ArrayList<SavingsAccount> savingsAccountArrayList,ArrayList<CurrentAccount> currentAccountArrayList){
        ArrayList<String> user_bankaccounts = new ArrayList<>();
        user_bankaccounts.add("Account to transfer to");

        for(SavingsAccount savingsAccount : savingsAccountArrayList){

            String account_information = "Account number: " + savingsAccount.getAccountnumber();
            user_bankaccounts.add((account_information));
            combined_accountnumbers.add(savingsAccount.getAccountnumber());
        }

        for(CurrentAccount currentAccount : currentAccountArrayList){

                String account_information = "Account number: " + currentAccount.getAccountnumber();
                user_bankaccounts.add(account_information);
                combined_accountnumbers.add(currentAccount.getAccountnumber());
        }
        return user_bankaccounts;
    }
}
