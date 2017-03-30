package com.example.simon.blue426;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by simon on 3/28/2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScanForDevicesBLE {



    LeDeviceListAdapter leDeviceListAdapter;
    Handler handler = new Handler();
    BluetoothAdapter theBluetoothAdapter;
    BluetoothLeScanner theLeScanner;
    private static final long SCAN_PERIOD = 10000;
    Activity theActivity;

    public ScanForDevicesBLE(Activity activity, LeDeviceListAdapter deviceListAdapter, BluetoothAdapter btAdapter){
        super();
        this.theActivity = activity;
        this.leDeviceListAdapter = deviceListAdapter;
        this.theBluetoothAdapter = btAdapter;
        this.theLeScanner = btAdapter.getBluetoothLeScanner();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(theActivity.getApplicationContext(),"Done scanning..."+Integer.toString(leDeviceListAdapter.getCount()),Toast.LENGTH_SHORT).show();
                    theLeScanner.stopScan(leScanCallback);

                }
            }, SCAN_PERIOD);
            Toast.makeText(theActivity.getApplicationContext(), "Scanning for devices...", Toast.LENGTH_SHORT).show();
            theLeScanner.startScan(leScanCallback);
        }
        else {
            Toast.makeText(theActivity.getApplicationContext(),"Stopping device scan...",Toast.LENGTH_SHORT).show();
            theLeScanner.stopScan(leScanCallback);
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            leDeviceListAdapter.addDevice(result.getDevice());
            leDeviceListAdapter.notifyDataSetChanged();
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("Failed", Integer.toString(errorCode));
        }
    };

}
