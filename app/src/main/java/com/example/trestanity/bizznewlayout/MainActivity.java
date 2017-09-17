package com.example.trestanity.bizznewlayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager sessionManager;

    //for side Navigation detail
    Button btnNearby,btnSearch, btnPrivate, btnPostIt;
    //ImageButton ibNearby, ibSearch, ibPrivate, ibPostIt;

    private View navheader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(getApplicationContext());

        MainDBHelper.PostFragment postFragment = new MainDBHelper.PostFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, postFragment,postFragment.getTag()).commit();

        //assigning id
//        ibNearby = (ImageButton) findViewById(R.id.ibNearby);
//        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
//        ibPrivate = (ImageButton) findViewById(R.id.ibPrivate);
//        ibPostIt = (ImageButton) findViewById(R.id.ibPostIt);
//
        btnNearby = (Button) findViewById(R.id.btnNearby);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnPrivate = (Button) findViewById(R.id.btnPrivate);
        btnPostIt = (Button) findViewById(R.id.btnPostIt);

        //setting background
//        ibNearby.setBackgroundResource(R.drawable.ic_clicked);
//        ibSearch.setBackgroundResource(R.drawable.ic_unclicked);
//        ibPrivate.setBackgroundResource(R.drawable.ic_unclicked);
//        ibPostIt.setBackgroundResource(R.drawable.ic_unclicked);

        btnNearby.setBackgroundColor(Color.parseColor("#1565c0"));
        btnSearch.setBackgroundColor(Color.parseColor("#1e88e5"));
        btnPrivate.setBackgroundColor(Color.parseColor("#1e88e5"));
        btnPostIt.setBackgroundColor(Color.parseColor("#1e88e5"));

        firstView();
        setAccountDetailsSideNav();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void firstView()
    {
        NearbyFragment nearbyFragment = new NearbyFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, nearbyFragment,nearbyFragment.getTag())
                .commit();
    }

    public void setAccountDetailsSideNav()
    {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navheader = navigationView.getHeaderView(0);
        TextView tvEmail = (TextView) navheader.findViewById(R.id.tvEmail);

        HashMap<String, String> user = sessionManager.getUserDetails();
        String username = user.get(SessionManager.KEY_USERNAME);
        tvEmail.setText(username);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            String userType = "";
            if(userType.equals("user"))
            {
                checkUser();
            }
            else
            {
                Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
                startActivity(intent);
                this.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager manager = getSupportFragmentManager();

        if (id == R.id.nav_nearby)
        {
            NearbyFragment nearbyFragment = new NearbyFragment();
            manager.beginTransaction().replace(R.id.content,
                    nearbyFragment,
                    nearbyFragment.getTag())
                    .commit();

            btnNearby.setBackgroundColor(Color.parseColor("#1565c0"));
            btnSearch.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1e88e5"));
            //btnNearby.setBackgroundColor(0x00000000); // no color
        }
        else if (id == R.id.nav_search)
        {
            SearchFragment searchFragment = new SearchFragment();
            manager.beginTransaction().replace(R.id.content,
                    searchFragment,
                    searchFragment.getTag())
                    .commit();

            btnNearby.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnSearch.setBackgroundColor(Color.parseColor("#1565c0"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1e88e5"));

        }
        else if (id == R.id.nav_private)
        {
            PrivateFragment privateFragment = new PrivateFragment();
            manager.beginTransaction().replace(R.id.content,
                    privateFragment,
                    privateFragment.getTag())
                    .commit();

            btnNearby.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnSearch.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1565c0"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1e88e5"));
        }
        else if (id == R.id.nav_post)
        {
            MainDBHelper.PostFragment postFragment = new MainDBHelper.PostFragment();
            manager.beginTransaction().replace(R.id.content,
                    postFragment,
                    postFragment.getTag())
                    .commit();

            btnNearby.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnSearch.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1565c0"));
        }
        else if(id == R.id.nav_logout)
        {
            sessionManager.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void btnClick(View view)
    {

        FragmentManager manager = getSupportFragmentManager();

        if(view==btnNearby)
        {
            NearbyFragment nearbyFragment = new NearbyFragment();
            manager.beginTransaction().replace(R.id.content,
                    nearbyFragment,
                    nearbyFragment.getTag())
                    .commit();
            btnNearby.setBackgroundColor(Color.parseColor("#1565c0"));
            btnSearch.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1e88e5"));
        }
        else if(view==btnSearch)
        {
            SearchFragment searchFragment = new SearchFragment();
            manager.beginTransaction().replace(R.id.content,
                    searchFragment,
                    searchFragment.getTag())
                    .commit();
            btnNearby.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnSearch.setBackgroundColor(Color.parseColor("#1565c0"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1e88e5"));
        }
        else if(view==btnPrivate)
        {
            PrivateFragment privateFragment = new PrivateFragment();
            manager.beginTransaction().replace(R.id.content,
                    privateFragment,
                    privateFragment.getTag())
                    .commit();

            btnNearby.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnSearch.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1565c0"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1e88e5"));
        }
        else if(view==btnPostIt)
        {
            MainDBHelper.PostFragment postFragment = new MainDBHelper.PostFragment();
            manager.beginTransaction().replace(R.id.content,
                    postFragment,
                    postFragment.getTag())
                    .commit();

            btnNearby.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnSearch.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPrivate.setBackgroundColor(Color.parseColor("#1e88e5"));
            btnPostIt.setBackgroundColor(Color.parseColor("#1565c0"));
        }

    }



    public void fetchUserInformation(final String username)
    {
        //fetch all important information of user
    }

    public void checkUser()
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Message");

        // set dialog message
        alertDialogBuilder
                .setMessage("Only Tenants can add places.")
                .setCancelable(true)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }
}
