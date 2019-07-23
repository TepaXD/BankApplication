package com.example.bankapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.example.bankapplication.ui.main.FragmentManageAccount;
import com.example.bankapplication.ui.main.FragmentAddAccount;
import com.example.bankapplication.ui.main.SectionsPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;

public class ManageAccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BankUser bankUser = null;
    DatabaseHelper db = new DatabaseHelper(this);

    //tab setup
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    FragmentManageAccount fragmentManageAccount = new FragmentManageAccount();
    FragmentAddAccount fragmentAddAccount = new FragmentAddAccount();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);


        //Tab setups

        bankUser = (BankUser) getIntent().getSerializableExtra("bank_user");
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        Bundle bundle = new Bundle();
        bundle.putSerializable("bank_user",bankUser);
        fragmentManageAccount.setArguments(bundle);
        fragmentAddAccount.setArguments(bundle);


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        //drawer setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(ManageAccountActivity.this, MainActivity.class);
            System.out.println(bankUser.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 10);
            finish();
        }
    }

    //method to pass the db for the fragments
    public DatabaseHelper getDb(){
        return db;
    }

   private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentManageAccount,"Manage accounts");
        adapter.addFragment(fragmentAddAccount,"Add account");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_account, menu);
        return true;
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(ManageAccountActivity.this,MainActivity.class);
            System.out.println("#####################" + bankUser.toString());
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 1);
            finish();
        } else if (id == R.id.nav_payments) {
            Intent intent = new Intent(ManageAccountActivity.this,PaymentsActivity.class);
            intent.putExtra("bank_user",bankUser);
            startActivityForResult(intent, 2);
            finish();
        } else if (id == R.id.nav_manage_user) {
            Intent intent = new Intent(ManageAccountActivity.this, ManageUser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bank_user", bankUser);
            startActivityForResult(intent, 3);
            finish();
        } else if (id == R.id.nav_signout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageAccountActivity.this);
            builder.setMessage("Are you sure you want to sign out?");
            // Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(ManageAccountActivity.this, LoginActivity.class);
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
