package com.example.simon.blue426;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by simon on 3/28/2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScanForDevicesBLE {



    LeDeviceListAdapter leDeviceListAdapter;
    Handler handler = new Handler();
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner leScanner = btAdapter.getBluetoothLeScanner();
    private static final long SCAN_PERIOD = 10000;
    Activity theActivity;

    public ScanForDevicesBLE(Activity activity, LeDeviceListAdapter deviceListAdapter){
        super();
        this.theActivity = activity;
        this.leDeviceListAdapter = deviceListAdapter;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    leScanner.stopScan(leScanCallback);
//                    Log.d("Size", Integer.toString(leDeviceListAdapter.getCount()));
                }
            }, SCAN_PERIOD);
            leScanner.startScan(leScanCallback);
        } else {
            leScanner.stopScan(leScanCallback);
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            leDeviceListAdapter.addDevice(result.getDevice());
            Log.d("onScanResult", "onScanResult: "+leDeviceListAdapter.getCount());
//            theActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("Device", result.getDevice().getName());
//                    leDeviceListAdapter.addDevice(result.getDevice());
//                    leDeviceListAdapter.notifyDataSetChanged();
//                }
//            });
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("Failed","Terribel Failure Sir!");
        }
    };
}
