package com.aslan.personalguardianapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aslan.personalguardianapp.R;
import com.aslan.personalguardianapp.util.Constants;
import com.aslan.personalguardianapp.util.OnFragmentInteractionListener;
import com.aslan.personalguardianapp.util.Utility;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private final SearchFragment SEARCH_FRAGMENT = new SearchFragment();

    private final MapFragment MAP_FRAGMENT = new MapFragment();

    private final SettingsFragment SETTINGS_FRAGMENT = new SettingsFragment();

    // UI components
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private boolean showSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Set the default fragment
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            //TODO ???
            int command = extras.getInt(Constants.COMMAND);
            showSettings = command == Constants.SHOW_SETTINGS;
        }

        if (showSettings) {
//            MAP_FRAGMENT.setArguments(getIntent().getExtras());
            changeFragment(SETTINGS_FRAGMENT);
            // Start sensors for the first time run.
//            Utility.startSensors(getApplicationContext(), true);
        } else {
//            boolean nonGrantedPermissionsExists = checkNonGrantedPermissions();
//            if (!nonGrantedPermissionsExists) {
            changeFragment(SEARCH_FRAGMENT);
            navigationView.setCheckedItem(R.id.nav_search);
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


//    /**
//     * Check for the non granted permissions and if there are any, move to the PermissionFramgment.
//     *
//     * @return true if there is atleast a non granted permission, false if all the permissions are granted.
//     */
//    private boolean checkNonGrantedPermissions() {
//        String[] nonGrantedPermissions = Utility.nonGrantedPermissions(getApplicationContext());
//
//        if (nonGrantedPermissions.length > 0) {
//            // There are some non granted permissions
//
//            // Disable user from moving to any other fragments without providing permissions
//            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//            toggle.setDrawerIndicatorEnabled(false);
//
//            // Show permission fragment
//            changeFragment(new PermissionFragment());
//
//            return true;
//        }
//        return false;
//    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;

        if (id == R.id.nav_map) {
            MAP_FRAGMENT.setArguments(getIntent().getExtras());
            fragment = MAP_FRAGMENT;
        } else if (id == R.id.nav_settings) {
            fragment = SETTINGS_FRAGMENT;
        } else if (id == R.id.nav_sign_out) {
            Utility.saveUserSignedIn(getApplicationContext(), false);
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
            return true;
        } else if (id == R.id.nav_exit) {
            MainActivity.this.finish();
            return true;
        } else {
            fragment = SEARCH_FRAGMENT;
        }

        changeFragment(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Change the active fragment to the given one.
     *
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }


    @Override
    public void onFragmentInteraction(Fragment fragment, String command) {
//        if (fragment instanceof PermissionFragment) {
//            if (Constants.ALL_PERMISSIONS_GRANTED.equals(command)) {
//                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                toggle.setDrawerIndicatorEnabled(true);
//                Utility.startSensors(getApplicationContext(), true);
//            }
//        } else if (fragment instanceof ProfileFragment) {
//            if (showSettings) {  // Only for the first time it will be true
//                showSettings = false;
//                checkNonGrantedPermissions();
//            }
//        }
    }
}
