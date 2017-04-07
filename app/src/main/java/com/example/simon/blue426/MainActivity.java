package com.example.simon.blue426;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    Menu theMenu;
    BluetoothAdapter btAdapter;
    AdapterLE leDeviceListAdapter;
    ScannerLE scannerLE;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    public static final String BLUETOOTH_MESSAGE = "NEW_DEVICE";



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView devices = (ListView) findViewById(R.id.btle_devices);
        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                scannerLE.scanLeDevice(false);
                BluetoothDevice device = (BluetoothDevice) view.getTag();
                Intent intent = new Intent(MainActivity.this, WorkActivity.class);
                intent.putExtra(BLUETOOTH_MESSAGE,device);
                startActivity(intent);
            }
        });


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        leDeviceListAdapter = new AdapterLE(this);
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
        this.scannerLE = new ScannerLE(this, leDeviceListAdapter, btAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.theMenu = menu;
        UpdateMenuOptions(menu);
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
            leDeviceListAdapter.clear();
            scannerLE.scanLeDevice(true);
        }
        if (id == R.id.bluetooth_status) {
            if (btAdapter.isEnabled()) {
                scannerLE.scanLeDevice(false);
                btAdapter.disable();
                invalidateOptionsMenu();
            }
            else if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                invalidateOptionsMenu();
            }
            else if(resultCode == RESULT_CANCELED) {
                invalidateOptionsMenu();
            }
        }
    }

    private void UpdateMenuOptions(Menu menu){
        MenuItem bluetoothSearch = menu.findItem(R.id.bluetooth_search);
        MenuItem bluetoothStatus = menu.findItem(R.id.bluetooth_status);
        if (btAdapter == null) {
            bluetoothSearch.setEnabled(false);
            bluetoothStatus.setEnabled(false);
        }
        if (btAdapter.isEnabled()) {
            bluetoothStatus.setChecked(true);
            bluetoothSearch.setEnabled(true);
        }
        else if (!btAdapter.isEnabled()) {
            bluetoothStatus.setChecked(false);
            bluetoothSearch.setEnabled(false);
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        UpdateMenuOptions(menu);
        return true;
    }


}






