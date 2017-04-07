package com.example.simon.blue426;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

/**
 * Created by simon on 3/30/2017.
 */

public class BluetoothService extends Service{

    public static final String DATA_SERVICE = "ACTION_SERVICE_FOUND";
    public static final String DATA_CHARACTERISTIC = "ACTION_CHARACTERISITIC_CHANGED";
    BluetoothDevice theDevice;
    Context theContext;
    BluetoothGatt theGatt;

    public static final String ACTION_GATT_CONNECTING = "ACTION_GATT_CONNECTING";
    public static final String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public static final String EXTRA_DATA = "EXTRA_DATA";

    private static final String SERVICE1 = "02366e80-cf3a-11e1-9ab4-0002a5d5c51b";
    private static final String CHARACTERISTIC1 = "e23e78a0-cf4a-11e1-8ffc-0002a5d5c51b";

    private ArrayList<BluetoothGattService> SERVICES;


    public BluetoothService(BluetoothDevice device, Context context){
        theDevice = device;
        theContext = context;
        SERVICES = new ArrayList<>();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void EndService(){
        theGatt.disconnect();
        theGatt.close();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void StartService(){

        theGatt = theDevice.connectGatt(theContext, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    broadcastUpdate(ACTION_GATT_CONNECTED);
                    theGatt.discoverServices();
                }
                if(newState == BluetoothProfile.STATE_CONNECTING){
                    broadcastUpdate(ACTION_GATT_CONNECTING);
                }
                else if (newState == STATE_DISCONNECTED) {
                    broadcastUpdate(ACTION_GATT_DISCONNECTED);
                    SERVICES.clear();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, gatt.getService(UUID.fromString(SERVICE1)));
                }
            }
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    Toast.makeText(theContext, "Write successful",Toast.LENGTH_LONG);
                }
                if(status == BluetoothGatt.GATT_FAILURE){
                    Toast.makeText(theContext, "Write failed",Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        theContext.sendBroadcast(intent);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void broadcastUpdate(final String action, final BluetoothGattService service) {
        Intent intent = new Intent(action);
        String serviceUUID = service.getUuid().toString();
        intent.putExtra(DATA_SERVICE,serviceUUID);
        theContext.sendBroadcast(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if(UUID.fromString(CHARACTERISTIC1).equals(characteristic.getUuid())){
            byte[] value = characteristic.getValue();
            intent.putExtra(DATA_CHARACTERISTIC, String.valueOf(value));
        }
        theContext.sendBroadcast(intent);


        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
//        }
//        else {
//            // For all other profiles, writes the data formatted in HEX.
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for(byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
//                        stringBuilder.toString());
//            }
//        }
//        theContext.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
