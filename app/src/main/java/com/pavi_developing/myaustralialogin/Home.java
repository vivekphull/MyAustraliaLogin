package com.pavi_developing.myaustralialogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;


public class Home extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("myAustralia");
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container,new HomeFragment());
        fragmentTransaction.commit();
        navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_id:
                        fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new HomeFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("myAustralia");
                        drawerLayout.closeDrawers();
                        item.setChecked(true);
                        break;

                    case R.id.history_id:
                        fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new HistoryFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("History");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.counsil_id:
                        fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new CounsilInfoFragment());
                        fragmentTransaction.commit();

                        getSupportActionBar().setTitle("Counsil");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.tellafriend_id:
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Public App");
                        intent.putExtra(Intent.EXTRA_TEXT,"Attach Link of the App here");
                        startActivity(Intent.createChooser(intent,"Share using"));
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.quickguide_id:
                        fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new QuickguideFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Quick Guide");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.aboutapp_id:
                        fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new AboutAppFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("About App");

                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.account_id:
                        /*
                        fragmentTransaction=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new AccountFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Account");
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        */
                        startActivity(new Intent(getApplicationContext(), Main.class).putExtra("directLogin", false));
                        finish();
                        break;

                }


                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
