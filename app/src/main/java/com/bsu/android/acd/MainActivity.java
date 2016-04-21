package com.bsu.android.acd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = getClass().getName();
    public static final String ADD_DEVICE = "com.bsu.android.acd.ADD_DEVICE";

    @Bind(R.id.devices_list)
    ListView devicesListView;
    @Bind(R.id.loading_spinner)
    View mLoadingSpinner;
    private int mShortAnimationDuration;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private DevicesAdapter devicesAdapter;
    private BroadcastReceiver mAddDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received devices");
            String devName = intent.getStringExtra(getString(R.string.deviceName));
            String devIp = intent.getStringExtra(getString(R.string.deviceIp));
            devicesAdapter.add(new Device(devName, devIp));
            crossfade();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        devicesListView.setVisibility(View.GONE);
        mLoadingSpinner.setVisibility(View.GONE);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mLoadingSpinner.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, NetworkService.class);
                startService(intent);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingSpinner.setVisibility(View.GONE);
                                fab.setVisibility(view.VISIBLE);
                            }
                        });
                    }
                }, 2*1000);
            }
        });

        ArrayList<Device> devices = new ArrayList<>();
        devicesAdapter = new DevicesAdapter(this, devices);
        //devicesAdapter.add(new Device("a", "b"));
        devicesListView.setAdapter(devicesAdapter);
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DeviceViewActivity.class);
                Device d = devicesAdapter.getItem(position);
                intent.putExtra("device", Parcels.wrap(d));
                startActivity(intent);
            }
        });
    }

    public void setVisibility(View v, int visibility) {
        v.setVisibility(visibility);
    }

    public void crossfade() {
        devicesListView.setAlpha(0f);
        devicesListView.setVisibility(View.VISIBLE);

        devicesListView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        if (mLoadingSpinner.getVisibility() == View.VISIBLE) {
            mLoadingSpinner.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mLoadingSpinner.setVisibility(View.GONE);
                        }
                    });
        }
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log) {
            Intent intent = new Intent(this, LogViewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.logTable){
            Intent intent = new Intent(this, LogTableViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(mAddDeviceReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(mAddDeviceReceiver, new IntentFilter(ADD_DEVICE));
        super.onResume();
    }
}

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, NetworkService.class);
//                startService(intent);
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });
//
//        ArrayList<Device> devices = new ArrayList<>();
//        devices.add(new Device("a","b"));
//        DevicesAdapter devicesAdapter = new DevicesAdapter(this, devices);
//        devicesListView.setAdapter(devicesAdapter);
//        //devicesListView.add
////        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
////        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
////                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
////        drawer.setDrawerListener(toggle);
////        toggle.syncState();
//
////        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
