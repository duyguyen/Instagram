package com.example.thanh.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    NavigationView mNavigationView;

    /*
     * mActionBarDrawerToggle : create a toggle button.
     * It ties together the DrawerLayout and the framework ActionBar.
     * setSupportActionBar adds a tool bar by android.support.v7.widget.Toolbar.
     * setDisplayHomeAsUpEnabled sets whether home should be displayed as an "up" affordance.
     * changeFragmentDisplay set fragment will be displayed when click on button.
     *  mNavigationView.setNavigationItemSelectedListener set listener when user click on one of MenuItem of nav.
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // layout variables
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        mNavigationView = findViewById(R.id.main_nav_view);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();


        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar)); // add toolbar for main activity
        getSupportActionBar().setTitle("Instagram");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_menu);

        /*
         * default fragment to be displayed
         * */
        changeFragmentDisplay(R.id.home);

        // listener for navigation view
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                /*
                 * change fragment display when the user click on one of the buttons
                 * */
                changeFragmentDisplay(menuItem.getItemId());
                return true;

            }
        });


    }


    /*
     * @param menuItem is what item in nav bar when the user click.
     * Depending on menuItem param, a new fragment will be set.
     * mDrawerLayout.closeDrawer to hide the nav bar again.
     * Use FragmentTransaction to replace the main_fragment_content by the fragment which
     * is set before by menuItem param. So this will show the fragment in the screen.
     * */
    private boolean changeFragmentDisplay(int menuItem) {

        Fragment fragment = null;

        if (menuItem == R.id.home) {
            fragment = new HomeFragment();

        } else if (menuItem == R.id.search) {
            fragment = new SearchFragment();

        } else if (menuItem == R.id.profile) {
            fragment = new ProfileFragment();

        } else if (menuItem == R.id.likes) {
            fragment = new LikesFragment();

        } else if (menuItem == R.id.camera) {
            fragment = new CameraFragment();

        } else if (menuItem == R.id.log_out) {

            SharePreferenceManager sharePreferenceManager = SharePreferenceManager.getInstance(getApplicationContext());

            // optional security layer
            if (sharePreferenceManager.isUserLogIn()) {
                sharePreferenceManager.logUserOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

        } else {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
        }

        // hide navigation drawer
        mDrawerLayout.closeDrawer(Gravity.START);

        /*
         * display on the screen
         * */

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_content, fragment);
            ft.commit(); // commit the change
        }

        return false;
    }

    /*
    * onOptionsItemSelected will get the item of nave menu whenever the user click on.
    *
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /*
    * onStart runs first, checking whether user login or not.
    * */
    @Override
    protected void onStart() {
        super.onStart();
        boolean isUserLogIn = SharePreferenceManager.getInstance(getApplicationContext()).isUserLogIn();
        if (!isUserLogIn){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

}
