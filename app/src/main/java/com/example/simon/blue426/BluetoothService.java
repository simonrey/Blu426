package com.example.simon.blue426;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Handler;

import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

/**
 * Created by simon on 3/30/2017.
 */

public class BluetoothService extends Service{

    public static final String DATA_SERVICE = "ACTION_SERVICE_FOUND";
    public static final String DATA_CHARACTERISTIC_VALUE = "ACTION_CHARACTERISITIC_CHANGED";
    public static final String DATA_CHARACTERISTIC_DESTINATION = "ACTION_CHARACTERISTIC_DESTINATION";


    BluetoothDevice theDevice;
    Context theContext;
    BluetoothGatt theGatt;

    ArrayList<Float> writeBuffer;

    private android.os.Handler handler = new android.os.Handler();


    public static final String ACTION_GATT_CONNECTING = "ACTION_GATT_CONNECTING";
    public static final String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";

    private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String SERVICE1 = "02366e80-cf3a-11e1-9ab4-0002a5d5c51b";
    private static final String CHARACTERISTIC1 = "e23e78a0-cf4a-11e1-8ffc-0002a5d5c51b";
    private static final String CHARACTERISTIC2 = "cd20c480-e48b-11e2-840b-0002a5d5c51b";
    private static final String CHARACTERISTIC3 = "01c50b60-e48c-11e2-a073-0002a5d5c51b";
    private static final String CHARACTERISTIC4 = "08366e80-cf3a-11e1-9ab4-0002a5d5c51b";

    private ArrayList<BluetoothGattService> SERVICES;

    private int counter = 0;
    private ListIterator<Float> writeIterator;

    public BluetoothService(BluetoothDevice device, Context context){
        theDevice = device;
        theContext = context;
        SERVICES = new ArrayList<>();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void EndService(){
//        super.onDestroy();
        SERVICES.clear();
//        writeBuffer.clear();
        theGatt.disconnect();
        theGatt.close();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void StartService(){

        theGatt = theDevice.connectGatt(theContext, false, new BluetoothGattCallback() {

            List<BluetoothGattCharacteristic> chars = new ArrayList<BluetoothGattCharacteristic>();


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
                    chars.add(gatt.getService(UUID.fromString(SERVICE1)).getCharacteristic(UUID.fromString(CHARACTERISTIC1)));
                    chars.add(gatt.getService(UUID.fromString(SERVICE1)).getCharacteristic(UUID.fromString(CHARACTERISTIC2)));
                    chars.add(gatt.getService(UUID.fromString(SERVICE1)).getCharacteristic(UUID.fromString(CHARACTERISTIC3)));
                    requestCharacteristics(gatt);
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, gatt.getService(UUID.fromString(SERVICE1)));
                }
            }
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
                broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    theGatt.setCharacteristicNotification(characteristic,true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    theGatt.writeDescriptor(descriptor);
                }
            }
            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
                chars.remove(chars.get(chars.size()-1));
                if(chars.size()>0){
                    requestCharacteristics(gatt);
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    if(!writeBuffer.isEmpty()) {
                        WriteMessage writeMessage = new WriteMessage(writeIterator.next());
                        writeIterator.remove();
                        handler.postDelayed(writeMessage,600);
                    }
                }
                if(status == BluetoothGatt.GATT_FAILURE){

                }
            }

            public void requestCharacteristics(BluetoothGatt gatt){
                gatt.readCharacteristic(chars.get(chars.size()-1));
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
            intent.putExtra(DATA_CHARACTERISTIC_DESTINATION,1);
            intent.putExtra(DATA_CHARACTERISTIC_VALUE,value);
        }
        if(UUID.fromString(CHARACTERISTIC2).equals(characteristic.getUuid())){
            byte [] value = characteristic.getValue();
            intent.putExtra(DATA_CHARACTERISTIC_DESTINATION,2);
            intent.putExtra(DATA_CHARACTERISTIC_VALUE,value);
        }
        if(UUID.fromString(CHARACTERISTIC3).equals(characteristic.getUuid())){
            byte [] value = characteristic.getValue();
            intent.putExtra(DATA_CHARACTERISTIC_DESTINATION,3);
            intent.putExtra(DATA_CHARACTERISTIC_VALUE,value);
        }
        theContext.sendBroadcast(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void SendMessage(ArrayList<Float> ValX) {
        writeBuffer = ValX;
        writeIterator = writeBuffer.listIterator();
        WriteMessage writeMessage = new WriteMessage(writeIterator.next());
        writeIterator.remove();
        writeMessage.run();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private class WriteMessage implements Runnable{
        float writeItem;

        private WriteMessage(float item){
            writeItem = item;
        }

        @Override
        public void run() {
            int a;
            byte[] b = new byte[4];
            float c = writeItem;

            a = Float.floatToRawIntBits(c);
            b[0] = (byte) (a & 0xff);
            b[1] = (byte) ((a >> 8) & 0xff);
            b[2] = (byte) ((a >> 16) & 0xff);
            b[3] = (byte) ((a >> 24) & 0xff);
            theGatt.getService(UUID.fromString(SERVICE1)).getCharacteristic(UUID.fromString(CHARACTERISTIC4)).setValue(b);
            theGatt.writeCharacteristic(theGatt.getService(UUID.fromString(SERVICE1)).getCharacteristic(UUID.fromString(CHARACTERISTIC4)));
        };
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
