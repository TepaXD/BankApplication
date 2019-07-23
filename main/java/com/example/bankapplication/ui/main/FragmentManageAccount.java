package com.example.bankapplication.ui.main;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bankapplication.BankUser;
import com.example.bankapplication.CurrentAccount;
import com.example.bankapplication.DatabaseHelper;
import com.example.bankapplication.ManageAccountActivity;
import com.example.bankapplication.R;
import com.example.bankapplication.SavingsAccount;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FragmentManageAccount extends Fragment {

    Spinner account_spinner;
    TextView account_name_view, account_money_view, account_withdrawlimit_view,cardnumber_view, card_paylimit_view, card_creditlimit_view;
    EditText account_name_edit, account_money_add, account_withdrawlimit_edit, card_paylimit_edit, card_creditlimit_edit;
    Switch add_card;
    RadioGroup add_card_radioGroup;
    RadioButton debit_card, credit_card;
    Button deletebutton, updatebutton;
    BankUser bankUser;
    DatabaseHelper db;
    ArrayList<String> allAccounts;
    ArrayList<SavingsAccount> savingsAccountArrayList;
    ArrayList<CurrentAccount> currentAccountArrayList;


    Float account_money;
    Float current_withdrawlimit;
    Float card_paylimit;
    Float card_creditlimit;
    int cardnumber;
    String creditlimit;
    String paylimit;
    String paylimit_credit;


    //fragment for managing existing accounts
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_accountmanage,container,false);
        account_spinner = view.findViewById(R.id.account_spinner);
        account_name_view = view.findViewById(R.id.account_name_view);
        account_money_view = view.findViewById(R.id.account_money_view);
        account_withdrawlimit_view = view.findViewById(R.id.account_withdrawlimit_view);
        cardnumber_view = view.findViewById(R.id.card_number_view);
        card_paylimit_view = view.findViewById(R.id.card_paylimit_view);
        card_creditlimit_view = view.findViewById(R.id.card_creditlimit_view);
        account_name_edit = view.findViewById(R.id.name_edit);
        account_money_add = view.findViewById(R.id.money_add_edit);
        account_withdrawlimit_edit = view.findViewById(R.id.paylimit_change_edit);
        card_paylimit_edit = view.findViewById(R.id.card_paylimit_edit);
        card_creditlimit_edit = view.findViewById(R.id.card_creditlimit_edit);
        add_card = view.findViewById(R.id.add_card_switch);
        add_card_radioGroup = view.findViewById(R.id.edit_card_radioGroup);
        debit_card = view.findViewById(R.id.radioButton_debit);
        credit_card = view.findViewById(R.id.radioButton_credit);
        deletebutton = view.findViewById(R.id.button_delete);
        updatebutton = view.findViewById(R.id.add_button);

        db = ((ManageAccountActivity)getActivity()).getDb();

        bankUser = (BankUser) getArguments().getSerializable("bank_user");
        savingsAccountArrayList = db.getSavingsAccounts(bankUser.getUsername());
        currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());

        allAccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);
        addToSpinner(allAccounts);


        account_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                savingsAccountArrayList = db.getSavingsAccounts(bankUser.getUsername());
                currentAccountArrayList = db.getCurrentAccounts(bankUser.getUsername());

                int arrayID = account_spinner.getSelectedItemPosition();
                String accountNumber = allAccounts.get(arrayID);

                if(arrayID == 0){
                    allAccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);
                    addToSpinner(allAccounts);
                }

                account_name_view.setText(null);
                account_money_view.setText(null);
                account_withdrawlimit_view.setText(null);
                cardnumber_view.setText(null);
                card_paylimit_view.setText(null);
                card_creditlimit_view.setText(null);
                account_money_add.setText(null);
                card_creditlimit_edit.setText(null);
                account_withdrawlimit_edit.setText(null);
                card_paylimit_view.setText(null);
                add_card.setEnabled(false);
                add_card.setChecked(false);
                add_card.setText("Add card to account");
                credit_card.setChecked(false);
                debit_card.setChecked(false);

                for (SavingsAccount savingsAccount : savingsAccountArrayList) {
                    if (accountNumber.matches(Integer.toString(savingsAccount.getAccountnumber()))) {
                        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                        account_money = savingsAccount.getMoney();
                        String money = decimalFormat.format(savingsAccount.getMoney());

                        account_name_view.setText(savingsAccount.getAccountname());
                        account_money_view.setText("Money: " + money + "€");
                        account_withdrawlimit_view.setText("Can't pay with this account");
                        add_card.setEnabled(false);
                        credit_card.setChecked(false);
                        debit_card.setChecked(false);
                        add_card.setText("Savings account can't have a card");
                        card_paylimit_view.setText("Can't use cards with this account");
                        card_creditlimit_view.setText("Can't use cards with this account");
                        cardnumber_view.setText("Can't use cards with this account");
                        return;

                    }
                }
                    for (CurrentAccount currentAccount : currentAccountArrayList) {
                        if (accountNumber.matches(Integer.toString(currentAccount.getAccountnumber()))) {
                            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                            account_money = currentAccount.getMoney();
                            current_withdrawlimit = currentAccount.getAccount_withdrawlimit();
                            String money = decimalFormat.format(currentAccount.getMoney());
                            String paylimit_account = decimalFormat.format(currentAccount.getDebit_paylimit());


                            account_name_view.setText(currentAccount.getAccountname());
                            account_money_view.setText("Money: " + money + "€");
                            account_withdrawlimit_view.setText("Paylimit: " + paylimit_account + "€");
                            add_card.setEnabled(true);

                            if (currentAccount.getCardnumber_debit() > -1) {
                                cardnumber = currentAccount.getCardnumber_debit();
                                add_card.setChecked(true);
                                add_card.setEnabled(false);
                                debit_card.setChecked(true);
                                credit_card.setEnabled(false);
                                credit_card.setChecked(false);

                                paylimit = decimalFormat.format(currentAccount.getDebit_paylimit());
                                card_paylimit_view.setText("Card paylimit: " + paylimit + "€");
                                cardnumber_view.setText("Cardnumber: " + currentAccount.getCardnumber_debit());
                                card_creditlimit_view.setText("No credit on debit cards");
                                return;
                            }

                            else if (currentAccount.getCardnumber_credit() > -1) {
                                cardnumber = currentAccount.getCardnumber_credit();
                                add_card.setChecked(true);
                                add_card.setEnabled(false);
                                credit_card.setChecked(true);
                                debit_card.setEnabled(false);
                                debit_card.setChecked(false);

                                paylimit_credit = decimalFormat.format(currentAccount.getCredit_paylimit());
                                creditlimit = decimalFormat.format(currentAccount.getCreditlimit());
                                card_paylimit_view.setText("Card paylimit: " + paylimit_credit + "€");
                                cardnumber_view.setText("Cardnumber: " + currentAccount.getCardnumber_credit());
                                card_creditlimit_view.setText("Card creditlimit: " + creditlimit + "€");
                                return;
                            } else {
                                cardnumber = currentAccount.getCardnumber_credit();
                            }

                        }
                    }
                }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        add_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == false){
                    credit_card.setClickable(false);
                    debit_card.setClickable(false);
                    credit_card.setChecked(false);
                    debit_card.setChecked(false);
                    card_paylimit_view.setText(null);
                    card_paylimit_view.setEnabled(false);
                    card_creditlimit_view.setText(null);
                    card_creditlimit_view.setEnabled(false);
                    card_paylimit_edit.setText(null);
                    card_paylimit_edit.setEnabled(false);
                    card_creditlimit_edit.setText(null);
                    card_creditlimit_edit.setEnabled(false);
                } else {
                    credit_card.setClickable(true);
                    debit_card.setClickable(true);
                    card_paylimit_view.setEnabled(true);
                    card_creditlimit_view.setEnabled(true);
                    card_paylimit_edit.setEnabled(true);
                    card_creditlimit_edit.setEnabled(true);
                }

            }
        });

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int arrayID = account_spinner.getSelectedItemPosition();
                String accountNumber = allAccounts.get(arrayID);

                if (accountNumber.matches("Select account")) {
                    Toast select_account = Toast.makeText(getContext(), "Select an account first", Toast.LENGTH_SHORT);
                    select_account.show();
                    return;
                }


                Float account_withdrawlimit;
                String accountname;
                Float money;
                String cardtype = null;
                Float creditlimit_card = null;
                Float paylimit_card = null;
                Toast update_success;
                int cardnumber_update = -1;

                //editing a current account
                if (Integer.parseInt(accountNumber) >= 20000000) {
                    if(cardnumber_view.getText().toString() != null && !cardnumber_view.getText().toString().isEmpty()) {
                        cardnumber_update = cardnumber;
                    }
                    if (account_withdrawlimit_edit != null && !account_withdrawlimit_edit.getText().toString().isEmpty()) {
                        account_withdrawlimit = Float.parseFloat(account_withdrawlimit_edit.getText().toString());
                    } else {
                        account_withdrawlimit = current_withdrawlimit;
                    }
                    if (account_name_edit.getText().toString() != null && !account_name_edit.getText().toString().isEmpty()) {
                        accountname = account_name_edit.getText().toString();
                    } else {
                        accountname = account_name_view.getText().toString();
                    }
                    if(account_money_add.getText().toString() != null && !account_money_add.getText().toString().isEmpty()) {
                        money = account_money + Float.parseFloat(account_money_add.getText().toString());
                        db.addTransaction(Integer.parseInt(accountNumber),money);
                    } else {
                        money = account_money;
                    }
                    if(debit_card.isChecked() == true || cardnumber >= 10000000 && cardnumber < 20000000){
                            cardtype = "debit";
                    } else if (credit_card.isChecked() == true || cardnumber >= 20000000) {
                        cardtype = "credit";
                    }
                    if(card_paylimit_edit.getText().toString() != null && !card_paylimit_edit.getText().toString().isEmpty()){
                        paylimit_card = Float.parseFloat(card_paylimit_edit.getText().toString());
                    } else if (card_paylimit_view.getText().toString() != null && !card_paylimit_view.getText().toString().isEmpty() && cardtype.matches("debit")){
                        paylimit_card = Float.parseFloat(paylimit);
                    } else if (card_paylimit_view.getText().toString() != null && !card_paylimit_view.getText().toString().isEmpty() && cardtype.matches("credit")){
                        paylimit_card = Float.parseFloat(paylimit_credit);
                    } else {
                        paylimit_card = card_paylimit;
                    }
                    if(card_creditlimit_edit.getText().toString() != null && !card_creditlimit_edit.getText().toString().isEmpty()){
                        creditlimit_card = Float.parseFloat(card_creditlimit_edit.getText().toString());
                    } else if (card_creditlimit_view.getText().toString() != null && !card_creditlimit_view.getText().toString().isEmpty() && cardtype.matches("credit")){
                        creditlimit_card = Float.parseFloat(creditlimit);
                    } else {
                        creditlimit_card = card_creditlimit;
                    }
                    db.setBankAccount(accountname, Integer.parseInt(accountNumber), money, account_withdrawlimit, cardnumber_update, cardtype, paylimit_card, creditlimit_card);

                }
                //editing a savings account
                else if (Integer.parseInt(accountNumber) < 20000000 && Integer.parseInt(accountNumber) >= 10000000){

                    if (account_name_edit.getText().toString() != null && !account_name_edit.getText().toString().isEmpty()) {
                        accountname = account_name_edit.getText().toString();
                    } else {
                        accountname = account_name_view.getText().toString();
                    }
                    if(account_money_add.getText().toString() != null && !account_money_add.getText().toString().isEmpty()) {
                        money = account_money + Float.parseFloat(account_money_add.getText().toString());
                        db.addTransaction(Integer.parseInt(accountNumber),Float.parseFloat(account_money_add.getText().toString()));
                    } else {
                        money = account_money;
                    }
                    db.setBankAccount(accountname, Integer.parseInt(accountNumber), money, null, cardnumber, null, null, null);
                }

                account_name_view.setText(null);
                account_name_edit.setText(null);
                account_money_view.setText(null);
                account_withdrawlimit_view.setText(null);
                cardnumber_view.setText(null);
                card_paylimit_view.setText(null);
                card_creditlimit_view.setText(null);
                account_money_add.setText(null);
                card_creditlimit_edit.setText(null);
                account_withdrawlimit_edit.setText(null);
                card_paylimit_view.setText(null);
                add_card.setEnabled(false);
                add_card.setChecked(false);
                add_card.setText("Add card to account");
                credit_card.setChecked(false);
                debit_card.setChecked(false);

                update_success = Toast.makeText(getContext(),"Account updated",Toast.LENGTH_SHORT);
                update_success.show();
                allAccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);
                addToSpinner(allAccounts);
            }
        });


        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int arrayID = account_spinner.getSelectedItemPosition();
                String accountNumber = allAccounts.get(arrayID);

                if (accountNumber.matches("Select account")) {
                    Toast select_account = Toast.makeText(getContext(), "Select an account first", Toast.LENGTH_SHORT);
                    select_account.show();
                    return;
                }

                db.deleteAccount(Integer.parseInt(accountNumber));

                account_name_view.setText(null);
                account_name_edit.setText(null);
                account_money_view.setText(null);
                account_withdrawlimit_view.setText(null);
                cardnumber_view.setText(null);
                card_paylimit_view.setText(null);
                card_creditlimit_view.setText(null);
                account_money_add.setText(null);
                card_creditlimit_edit.setText(null);
                account_withdrawlimit_edit.setText(null);
                card_paylimit_view.setText(null);
                add_card.setEnabled(false);
                add_card.setChecked(false);
                add_card.setText("Add card to account");
                credit_card.setChecked(false);
                debit_card.setChecked(false);

               Toast delete_success = Toast.makeText(getContext(),"Account deleted",Toast.LENGTH_SHORT);
                delete_success.show();
                allAccounts = createCombinedAccountList(savingsAccountArrayList,currentAccountArrayList);
                addToSpinner(allAccounts);
                account_spinner.setSelection(0);
            }
        });

        return view;
    }

    //Takes in two arraylists that have different accounts tied to an username, and combines them into one arraylist. Then
    //returns this arraylist with all accounts
    public ArrayList<String> createCombinedAccountList(ArrayList<SavingsAccount> savingsAccountArrayList,ArrayList<CurrentAccount> currentAccountArrayList){
        ArrayList<String> user_bankaccounts = new ArrayList<>();

        user_bankaccounts.add("Select account");

        for(SavingsAccount savingsAccount : savingsAccountArrayList){
            user_bankaccounts.add(Integer.toString(savingsAccount.getAccountnumber()));
        }

        for(CurrentAccount currentAccount : currentAccountArrayList){
            user_bankaccounts.add(Integer.toString(currentAccount.getAccountnumber()));
        }

        return user_bankaccounts;
    }

    //method that creates the spinner adapter
    public void addToSpinner(ArrayList<String> allAccounts){
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,allAccounts);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        account_spinner.setAdapter(spinner_adapter);
    }
}
