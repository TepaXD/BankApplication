package com.example.bankapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    private String sql_file = "bankdatabase.sql";
    private static String db_file = "bankapplication.db";

    private String TABLE_BANK = "bank";
    private String BANK_COL1 = "bankid";
    private String BANK_COL2 = "bank_name";

    private String TABLE_USER = "bank_user";
    private String USER_COL1 = "username";
    private String USER_COL2 = "password";
    private String USER_COL3 = "name";
    private String USER_COL4 = "address";
    private String USER_COL5 = "phonenumber";
    private String USER_COL6 = "bankid";

    private String TABLE_SAVINGS = "savings_account";
    private String SAVINGS_COL1 = "accountnumber";
    private String SAVINGS_COL2 = "username";
    private String SAVINGS_COL3 = "money";
    private String SAVINGS_COL4 = "accountname";

    private String TABLE_CURRENT = "current_account";
    private String CURRENT_COL1 = "accountnumber";
    private String CURRENT_COL2 = "username";
    private String CURRENT_COL3 = "money";
    private String CURRENT_COL4 = "accountname";
    private String CURRENT_COL5 = "withdrawlimit";

    private String TABLE_CREDIT = "credit";
    private String CREDIT_COL1 = "cardnumber_credit";
    private String CREDIT_COL2 = "accountnumber";
    private String CREDIT_COL3 = "creditlimit";
    private String CREDIT_COL4 = "paylimit";

    private String TABLE_DEBIT = "debit";
    private String DEBIT_COL1 = "cardnumber_debit";
    private String DEBIT_COL2 = "accountnumber";
    private String DEBIT_COL3 = "paylimit";

    private String TABLE_ADMIN = "bank_admin";
    private String ADMIN_COL1 = "username";
    private String ADMIN_COL2 = "password";
    private String ADMIN_COL3 = "bankid";

    private String TABLE_TRANSACTIONS = "transactions";
    private String TRANSACTIONS_COL1 = "accountnumber_current";
    private String TRANSACTIONS_COL2 = "accountnumber_savings";
    private String TRANSACTIONS_COL3 = "money";

    public DatabaseHelper(Context context) {
        super(context, db_file, null, 31);
        this.context = context;


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            InputStream inputStream = context.getAssets().open(sql_file);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";


            //Read the sql file and run its contents
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.getProperty("line.separator"));
                if (line.contains(";")) {
                    String sql_query = stringBuilder.toString();
                    db.execSQL(sql_query);
                    stringBuilder.setLength(0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int version_old, int version_current) {
        if (version_old < version_current) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BANK);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDIT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBIT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
            onCreate(db);
        }

    }


    public ArrayList<BankUser> getBankUsers() {
        ArrayList<BankUser> bankUserArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bank_user", null);
        if (cursor.moveToFirst()) {
            do {
                bankUserArrayList.add(new BankUser(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Integer.parseInt(cursor.getString(5))));
            } while (cursor.moveToNext());
        }
        return bankUserArrayList;
    }


    public ArrayList<CurrentAccount> getCurrentAccounts(String bank_username) {
        ArrayList<CurrentAccount> currentAccountsArrayList = new ArrayList<>();
        int credit_number;
        int debit_number;
        float credit_paylimit;
        float credit_creditlimit;
        float debit_paylimit;

        String sql_query = "SELECT * FROM current_account LEFT OUTER JOIN credit ON credit.accountnumber = current_account.accountnumber LEFT OUTER JOIN debit ON debit.accountnumber = current_account.accountnumber WHERE username =?";
        String sql_args[] = new String[]{bank_username};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql_query, sql_args, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(5) == null) {
                credit_number = -1;
            } else {
                credit_number = Integer.parseInt(cursor.getString(5));
            }
            if (cursor.getString(9) == null) {
                debit_number = -1;
            } else {
                debit_number = Integer.parseInt(cursor.getString(9));
            }
            if (cursor.getString(7) == null) {
                credit_paylimit = 0;
            } else {
                credit_paylimit = Integer.parseInt(cursor.getString(7));
            }
            if (cursor.getString(8) == null) {
                credit_creditlimit = 0;
            } else {
                credit_creditlimit = Integer.parseInt(cursor.getString(8));
            }
            if (cursor.getString(11) == null) {
                debit_paylimit = 0;
            } else {
                debit_paylimit = Integer.parseInt(cursor.getString(11));
            }
            currentAccountsArrayList.add(new CurrentAccount(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Float.parseFloat(cursor.getString(2)), cursor.getString(3), Float.parseFloat(cursor.getString(4)), credit_number, credit_paylimit, credit_creditlimit, debit_number, debit_paylimit));
        }

        return currentAccountsArrayList;
    }

    public ArrayList<SavingsAccount> getSavingsAccounts(String bank_username) {
        ArrayList<SavingsAccount> savingsAccountsArrayList = new ArrayList<>();
        String sql_query = "SELECT * FROM savings_account WHERE username = ?";
        String sql_args[] = new String[]{bank_username};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql_query, sql_args);
        while (cursor.moveToNext()) {
            savingsAccountsArrayList.add(new SavingsAccount(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Float.parseFloat(cursor.getString(2)), cursor.getString(3)));
        }

        return savingsAccountsArrayList;
    }

    public ArrayList<Bank> getBanks(String bank_username) {
        ArrayList<Bank> bankArrayList = new ArrayList<>();
        String sql_args[] = new String[]{bank_username};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT bankname, bank.bankid FROM bank INNER JOIN bank_user ON bank_user.bankid = bank.bankid WHERE username =?", sql_args);
        while (cursor.moveToNext()) {
            bankArrayList.add(new Bank(cursor.getString(0), Integer.parseInt(cursor.getString(1))));

        }

        return bankArrayList;
    }

    public ArrayList<Transaction> getTransactions(int accountnumber) {
        ArrayList<Transaction> transactionArrayList = new ArrayList<>();
        String sql_args[] = new String[]{Integer.toString(accountnumber)};
        SQLiteDatabase db = this.getReadableDatabase();

        if (accountnumber >= 20000000) {
            Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE " + TRANSACTIONS_COL1 + " =?", sql_args);
            while (cursor.moveToNext()) {
                transactionArrayList.add(new Transaction(Integer.parseInt(cursor.getString(0)), Float.parseFloat(cursor.getString(2))));
            }
        } else {
            Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE " + TRANSACTIONS_COL2 + " =?", sql_args);
            while (cursor.moveToNext()) {
                transactionArrayList.add(new Transaction(Integer.parseInt(cursor.getString(1)), Float.parseFloat(cursor.getString(2))));
            }
        }
        return transactionArrayList;
    }

    public ArrayList<BankAdmin> getBankAdmins() {
        ArrayList<BankAdmin> bankAdminArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bank_admin", null);
        while (cursor.moveToNext()) {
            bankAdminArrayList.add(new BankAdmin(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2))));

        }

        return bankAdminArrayList;
    }

    public boolean addBankUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL1, username);
        contentValues.put(USER_COL2, password);
        long result = db.insert("bank_user", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addSavingsAccount(String username, String money, String accountnumber, String accountname) {
        SQLiteDatabase db = this.getWritableDatabase();
        String accountnumber_saving = "1" + accountnumber;

        ContentValues contentValues = new ContentValues();
        contentValues.put(SAVINGS_COL1, Integer.parseInt(accountnumber_saving));
        contentValues.put(SAVINGS_COL2, username);
        if (money != null && !money.isEmpty()) {
            contentValues.put(SAVINGS_COL3, Float.parseFloat(money));
        } else {
            contentValues.put(SAVINGS_COL3, 0);
        }
        if (accountname != null && !accountname.isEmpty()) {
            contentValues.put(SAVINGS_COL4, accountname);
        } else {
            contentValues.put(SAVINGS_COL4, "Savings account");
        }
        long result = db.insert("savings_account", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addCurrentAccount(String username, String money, String accountnumber, String accountname, String paylimit) {
        SQLiteDatabase db = this.getWritableDatabase();

        String accountnumber_current = "2" + accountnumber;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CURRENT_COL1, Integer.parseInt(accountnumber_current));
        contentValues.put(CURRENT_COL2, username);
        if (money != null && !money.isEmpty()) {
            contentValues.put(CURRENT_COL3, Float.parseFloat(money));
        } else {
            contentValues.put(CURRENT_COL3, 0);
        }
        if (accountname != null && !accountname.isEmpty()) {
            contentValues.put(CURRENT_COL4, accountname);
        } else {
            contentValues.put(CURRENT_COL4, "Current account");
        }
        if (paylimit != null && !paylimit.isEmpty()) {
            contentValues.put(CURRENT_COL5, Integer.parseInt(paylimit));
        } else {
            contentValues.put(CURRENT_COL5, 0);
        }

        long result = db.insert("current_account", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addCreditCard(String paylimit, String creditlimit, String accountnumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        Random random = new Random();
        int creditcardnumber = Integer.parseInt("2" + random.nextInt(9999999) + 1);

        if(Integer.parseInt(accountnumber) < 20000000) {
            accountnumber = "2" + accountnumber;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(CREDIT_COL1, creditcardnumber);
        contentValues.put(CREDIT_COL2, Integer.parseInt(accountnumber));
        if (creditlimit != null && !creditlimit.isEmpty()) {
            contentValues.put(CREDIT_COL3, Float.parseFloat(creditlimit));
        } else {
            contentValues.put(CREDIT_COL3, 0);
        }
        if (paylimit != null && !paylimit.isEmpty()) {
            contentValues.put(CREDIT_COL4, Float.parseFloat(paylimit));
        } else {
            contentValues.put(CREDIT_COL4, 0);
        }

        long result = db.insert("credit", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addDebitCard(String paylimit, String accountnumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        Random random = new Random();
        int creditcardnumber = Integer.parseInt("1" + random.nextInt(9999999) + 1);

        if(Integer.parseInt(accountnumber) < 20000000) {
            accountnumber = "2" + accountnumber;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DEBIT_COL1, creditcardnumber);
        contentValues.put(DEBIT_COL2, accountnumber);
        if (paylimit != null && !paylimit.isEmpty()) {
            contentValues.put(DEBIT_COL3, Float.parseFloat(paylimit));
        } else {
            contentValues.put(DEBIT_COL3, 0);
        }

        long result = db.insert("debit", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void addTransaction(int accountnumber, Float money){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        System.out.println(accountnumber + "€€€");
        System.out.println(money + "%%%%");

        if(accountnumber >= 20000000){
            contentValues.put(TRANSACTIONS_COL1, accountnumber);
            contentValues.put(TRANSACTIONS_COL3, money);
        } else {
            contentValues.put(TRANSACTIONS_COL2, accountnumber);
            contentValues.put(TRANSACTIONS_COL3, money);
        }

        db.insert("transactions", null, contentValues);
    }

    public void setBankUser(String username, String name, String phonenumber, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql_args[] = new String[]{name, address, phonenumber, username};
        String sql_query = "UPDATE bank_user SET " + USER_COL3 + " = ?, " + USER_COL4 + " = ?, " + USER_COL5 + " =? WHERE " + USER_COL1 + " = ?";
        db.execSQL(sql_query, sql_args);

    }

    //generate a random accountnumber for the bank accounts
    public String generateAccountNumber() {
        String accountnumber;

        Random random = new Random();
        accountnumber = Integer.toString(random.nextInt(8000000) + 1000001);

        return accountnumber;
    }

    public void setBankAccount(String accountname, int accountnumber, Float money, Float withdrawlimit_account, int cardnumber, String card_type, Float paylimit_card, Float creditlimit_card) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql_query_account;
        String sql_query_card;


        if (accountnumber >= 20000000) {
            String sql_args[] = new String[]{Float.toString(money), accountname, Float.toString(withdrawlimit_account),Integer.toString(accountnumber)};
            sql_query_account = "UPDATE current_account SET " + CURRENT_COL3 + " = ?, " + CURRENT_COL4 + " = ?, " + CURRENT_COL5 + " =?  WHERE " + CURRENT_COL1 + " = ?";
            if (card_type != null && card_type.matches("credit")) {
                if (cardnumber == -1) {
                    addCreditCard(Float.toString(paylimit_card), Float.toString(creditlimit_card), Integer.toString(accountnumber));
                }
                    sql_query_card = "UPDATE credit SET " + CREDIT_COL3 + " = ?, " + CREDIT_COL4 + " = ? WHERE " + SAVINGS_COL1 + " = ?";
                    String sql_args_card[] = new String[]{Float.toString(creditlimit_card), Float.toString(paylimit_card),Integer.toString(accountnumber)};
                    db.execSQL(sql_query_card,sql_args_card);
                } else if (card_type != null && card_type.matches("debit")) {
                    if (cardnumber == -1) {
                        addDebitCard(Float.toString(paylimit_card), Integer.toString(accountnumber));
                    }
                    sql_query_card = "UPDATE debit  SET " + DEBIT_COL3 + " =? WHERE " + SAVINGS_COL1 + " = ?";
                    String sql_args_card[] = new String[]{Float.toString(paylimit_card),Integer.toString(accountnumber)};
                    db.execSQL(sql_query_card, sql_args_card);
                }
                db.execSQL(sql_query_account, sql_args);
            } else {
                sql_query_account = "UPDATE savings_account  SET " + SAVINGS_COL3 + " = ?, " + SAVINGS_COL4 + " = ? WHERE " + SAVINGS_COL1 + " = ?";
                String sql_args[] = new String[]{Float.toString(money), accountname,Integer.toString(accountnumber)};
                db.execSQL(sql_query_account, sql_args);
            }


        }

        public void deleteAccount(int accountnumber){
            SQLiteDatabase db = this.getWritableDatabase();
            String sql_query_account;

            if (accountnumber >= 20000000) {
                String sql_args[] = new String[]{Integer.toString(accountnumber)};
                sql_query_account = "DELETE FROM current_account" + " WHERE " + CURRENT_COL1 + " = ?";
                db.execSQL(sql_query_account, sql_args);
            } else if (accountnumber < 20000000 && accountnumber >= 10000000) {
                String sql_args[] = new String[]{Integer.toString(accountnumber)};
                sql_query_account = "DELETE FROM savings_account" + " WHERE " + CURRENT_COL1 + " = ?";
                db.execSQL(sql_query_account, sql_args);
            }
        }

    public void deleteUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql_query_account;

            String sql_args[] = new String[]{username};
            sql_query_account = "DELETE FROM bank_user" + " WHERE " + USER_COL1 + " = ?";
            db.execSQL(sql_query_account, sql_args);
        }

    public void deleteCard(int accountnumber, int cardnumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql_query_account;

        String sql_args[] = new String[]{Integer.toString(accountnumber)};

        if (cardnumber >= 20000000) {
            sql_query_account = "DELETE FROM credit" + " WHERE " + CREDIT_COL2 + " = ?";
         } else {
            sql_query_account = "DELETE FROM debit" + " WHERE " + DEBIT_COL2 + " = ?";

        }
        db.execSQL(sql_query_account, sql_args);
    }


        //smaller, more compact, method for setting the new balance of an account after a transaction
        public void paymentAccount(float new_money, int accountnumber){
            SQLiteDatabase db = this.getWritableDatabase();
            String sql_args[] = new String[]{Float.toString(new_money), Integer.toString(accountnumber)};
            if(accountnumber >= 20000000) {
                String sql_query = "UPDATE current_account SET " + CURRENT_COL3 + " =? WHERE " + CURRENT_COL1 + " =?";
                db.execSQL(sql_query,sql_args);
            } else {
                String sql_query = "UPDATE savings_account SET " + CURRENT_COL3 + " =? WHERE " + CURRENT_COL1 + " =?";
                db.execSQL(sql_query,sql_args);
            }

        }

    }
