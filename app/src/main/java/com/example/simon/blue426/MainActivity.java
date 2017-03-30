package com.example.simon.blue426;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Menu theMenu;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    LeDeviceListAdapter leDeviceListAdapter;
    ScanForDevicesBLE scanForDevicesBLE;


    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        leDeviceListAdapter = new LeDeviceListAdapter(this);
        ListView devices = (ListView)findViewById(R.id.btle_devices);
        devices.setAdapter(leDeviceListAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.scanForDevicesBLE = new ScanForDevicesBLE(this,leDeviceListAdapter,btAdapter);

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem bluetoothSearch = menu.findItem(R.id.bluetooth_search);
        MenuItem bluetoothStatus = menu.findItem(R.id.bluetooth_status);
        this.theMenu = menu;
        if (btAdapter == null) {
            bluetoothSearch.setEnabled(false);
            bluetoothStatus.setEnabled(false);
        }
        if (btAdapter.isEnabled()) {
            bluetoothStatus.setChecked(true);
            bluetoothSearch.setEnabled(true);
        } else if (!btAdapter.isEnabled()) {
            bluetoothStatus.setChecked(false);
            bluetoothSearch.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.bluetooth_search) {
//            Toast.makeText(this, Integer.toString(leDeviceListAdapter.getCount()), Toast.LENGTH_SHORT).show();
            leDeviceListAdapter.clear();
            scanForDevicesBLE.scanLeDevice(true);
        }
        if (id == R.id.bluetooth_status) {
            if (btAdapter.isEnabled()) {
                scanForDevicesBLE.scanLeDevice(false);
                btAdapter.disable();
                theMenu.findItem(R.id.bluetooth_status).setChecked(false);
                theMenu.findItem(R.id.bluetooth_search).setEnabled(false);
            } else if (!btAdapter.isEnabled()) {
                theMenu.findItem(R.id.bluetooth_status).setChecked(true);
                theMenu.findItem(R.id.bluetooth_search).setEnabled(true);
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        return super.onOptionsItemSelected(item);
    }


}






