package com.example.bankapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    EditText username_field, password_field, confirm_code_input;
    TextView confirm_code;
    Button login, register;
    DatabaseHelper db;
    ArrayList<BankUser> bankUserArrayList;
    ArrayList<BankAdmin> bankAdminArrayList;

    //OnCreate with listeners for the buttons, random securitycode generator and toasts for invalid entries
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username_field = findViewById(R.id.username);
        password_field = findViewById(R.id.password);
        confirm_code = findViewById(R.id.confirm_code);
        confirm_code_input = findViewById(R.id.confirm_code_input);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        db = new DatabaseHelper(this);
        bankUserArrayList = db.getBankUsers();
        bankAdminArrayList = db.getBankAdmins();

        register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String username = username_field.getText().toString();
                String password = password_field.getText().toString();
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
                        bankUserArrayList = db.getBankUsers();
                    } else {
                        Toast toast_failure = Toast.makeText(context, "Error in registering a user.", Toast.LENGTH_SHORT);
                        toast_failure.show();
                    }
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String username = username_field.getText().toString();
                String password = password_field.getText().toString();
                Context context = getApplicationContext();


                if (username.matches("")) {
                    Toast toast_login_username = Toast.makeText(context,"Please give your username first.",Toast.LENGTH_SHORT);
                    toast_login_username.show();
                } else if(password.equals("")){
                    Toast toast_login_password = Toast.makeText(context,"Please enter your password.",Toast.LENGTH_SHORT);
                    toast_login_password.show();
                }

                for(BankAdmin admin : bankAdminArrayList){
                    if(username.toLowerCase().equals(admin.getUsername().toLowerCase())){
                        if(password.equals(admin.getPassword())){
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("key", "value");
                            startActivityForResult(intent, 50);
                            finish();
                            return;
                        }
                    }
                }
                for (BankUser bankUser : bankUserArrayList){
                    if(username.toLowerCase().equals(bankUser.getUsername().toLowerCase())){
                        if(password.equals(bankUser.getPassword())){
                            if(confirm_code.getText().toString().isEmpty()){
                                Random random = new Random();
                                int confirmcode = random.nextInt(999999);
                                confirm_code.setText(Integer.toString(confirmcode));
                                confirm_code_input.setEnabled(true);
                                confirm_code_input.setHint("Enter the code above");
                                return;
                            } else if(!confirm_code_input.getText().toString().matches(confirm_code.getText().toString())) {
                                Random random = new Random();
                                int confirmcode = random.nextInt(999999);
                                confirm_code.setText(Integer.toString(confirmcode));
                                confirm_code_input.setText(null);
                                Toast login_failure = Toast.makeText(context,"Confirmation code doesn't match the given code", Toast.LENGTH_SHORT);
                                login_failure.show();
                                return;
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("bank_user", bankUser);
                                startActivityForResult(intent, 1);
                                finish();
                                return;
                            }
                        }
                    }
                }


                Toast toast_invalid_login = Toast.makeText(context,"Invalid credentials on login, please try again.",Toast.LENGTH_SHORT);
                toast_invalid_login.show();
            }
        });
    }
}
