package com.example.bankapplication.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bankapplication.BankUser;
import com.example.bankapplication.DatabaseHelper;
import com.example.bankapplication.ManageAccountActivity;
import com.example.bankapplication.R;

public class FragmentAddAccount extends Fragment {

    EditText add_accountname, add_money, add_accountpaylimit, add_cardpaylimit, add_cardcreditlimit;
    Switch add_newAccount_card;
    RadioGroup card_radioGroup, account_radioGroup;
    RadioButton newSavingAccount, newCurrentAccount, newAccount_debit_card, newAccount_credit_card;
    Button addbutton;
    DatabaseHelper db;
    String accountnumber;
    BankUser bankUser;

    //fragment for adding new accounts for the user
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accountadd,container,false);
        add_accountname = view.findViewById(R.id.add_accountname);
        add_money = view.findViewById(R.id.add_money);
        add_accountpaylimit = view.findViewById(R.id.add_paylimit);
        add_cardcreditlimit = view.findViewById(R.id.add_creditlimit);
        add_cardpaylimit = view.findViewById(R.id.add_card_paylimit);
        add_newAccount_card = view.findViewById(R.id.add_newAccount_card);
        card_radioGroup = view.findViewById(R.id.card_radioGroup);
        account_radioGroup = view.findViewById(R.id.account_radioGroup);
        newAccount_credit_card = view.findViewById(R.id.radioButton_addCredit);
        newAccount_debit_card = view.findViewById(R.id.radioButton_addDebit);
        newSavingAccount = view.findViewById(R.id.radioButton_saving);
        newCurrentAccount = view.findViewById(R.id.radiobutton_current);
        addbutton = view.findViewById(R.id.add_button);

        db = ((ManageAccountActivity)getActivity()).getDb();

        bankUser = (BankUser) getArguments().getSerializable("bank_user");

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
                        Toast toast_success = Toast.makeText(getContext(), "Savings account created successfully.", Toast.LENGTH_SHORT);
                        toast_success.show();
                    } else {
                        Toast toast_failure = Toast.makeText(getContext(), "Error in creating savings account.", Toast.LENGTH_SHORT);
                        toast_failure.show();
                    }

                } else if(newCurrentAccount.isChecked() == true){
                    boolean state_current = db.addCurrentAccount(username,money,accountnumber,accountname,paylimit);
                    if(newAccount_debit_card.isChecked() == true){
                        boolean state_debit = db.addDebitCard(cardpaylimit,accountnumber);
                        if (state_debit == true && state_current == true) {
                            Toast toast_success = Toast.makeText(getContext(), "Current account created successfully with a debit card.", Toast.LENGTH_SHORT);
                            toast_success.show();
                        } else {
                            Toast toast_failure = Toast.makeText(getContext(), "Error in creating a current account with debit card.", Toast.LENGTH_SHORT);
                            toast_failure.show();
                        }
                    } else if(newAccount_credit_card.isChecked() == true){
                        boolean state_credit = db.addCreditCard(cardpaylimit,cardcreditlimit,accountnumber);
                        if (state_credit == true && state_current == true) {
                            Toast toast_success = Toast.makeText(getContext(), "Current account created successfully with a credit card.", Toast.LENGTH_SHORT);
                            toast_success.show();
                        } else {
                            Toast toast_failure = Toast.makeText(getContext(), "Error in creating a current account with credit card.", Toast.LENGTH_SHORT);
                            toast_failure.show();
                        }
                    } else {
                        if (state_current == true) {
                            Toast toast_success = Toast.makeText(getContext(), "Current account created successfully.", Toast.LENGTH_SHORT);
                            toast_success.show();
                        } else {
                            Toast toast_failure = Toast.makeText(getContext(), "Error in creating a current account.", Toast.LENGTH_SHORT);
                            toast_failure.show();
                        }
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


        return view;
    }
}
