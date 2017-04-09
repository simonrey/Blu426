package com.example.simon.blue426;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkActivity extends AppCompatActivity {

    private Context theContext;

    public static final String BLUETOOTH_MESSAGE = "NEW_DEVICE";
    public static BluetoothDevice BLUETOOTH_DEVICE;
    public static String BLUETOOTH_NAME;
    public static String BLUETOOTH_MAC;
    public static IntentFilter filter = new IntentFilter();

    private TextView charX;
    private TextView charY;
    private TextView charZ;
    private ExpandableListView servicesWindow;
    private ListView characteristicView;

    private Integer count = 0;

    public List<String> servicesList;
    public ArrayList<Float> ValX;
    public ArrayList<Float> ValY;
    public ArrayList<Float> ValZ;

    public ArrayAdapter adapter;

    private BluetoothService BLUETOOTH_SERVICE;
    private static Menu theMenu;
    private BroadcastReceiver GattUpdate;
    private enum MenuState{
        DISCONNECTED, CONNECTING, CONNECTED, UPLOADING, STOPPED, PAUSED, RESUMED
    }
    private static MenuState CURRENT_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        theContext = this.getApplicationContext();

        charX = (TextView) findViewById(R.id.charX);
        charY = (TextView) findViewById(R.id.charY);
        charZ = (TextView) findViewById(R.id.charZ);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        BLUETOOTH_DEVICE = getIntent().getParcelableExtra(BLUETOOTH_MESSAGE);
        BLUETOOTH_NAME = BLUETOOTH_DEVICE.getName();
        BLUETOOTH_MAC = BLUETOOTH_DEVICE.getAddress();
        CURRENT_STATE = MenuState.DISCONNECTED;

        SetActionBarTitles();

        filter.addAction(BLUETOOTH_SERVICE.ACTION_DATA_AVAILABLE);
        filter.addAction(BLUETOOTH_SERVICE.ACTION_GATT_CONNECTED);
        filter.addAction(BLUETOOTH_SERVICE.ACTION_GATT_CONNECTING);
        filter.addAction(BLUETOOTH_SERVICE.ACTION_GATT_DISCONNECTED);
        filter.addAction(BLUETOOTH_SERVICE.ACTION_GATT_SERVICES_DISCOVERED);

        ValX = new ArrayList<>();
        ValY = new ArrayList<>();
        ValZ = new ArrayList<>();
        servicesList = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
    }

    @Override
    public void onBackPressed()
    {
        BLUETOOTH_SERVICE.EndService();
        super.onBackPressed();
    }

    @Override
    protected void onResume(){
        super.onResume();

        GattUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                Log.d("Action",action);
                if(BLUETOOTH_SERVICE.ACTION_GATT_CONNECTED.equals(action)){
                    CURRENT_STATE = MenuState.CONNECTED;
                    invalidateOptionsMenu();
                }
                if(BLUETOOTH_SERVICE.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                    CURRENT_STATE = MenuState.CONNECTED;
                    invalidateOptionsMenu();
                    String service = intent.getStringExtra(BLUETOOTH_SERVICE.DATA_SERVICE);
                    serviceListAdd(service);
                }
                if(BLUETOOTH_SERVICE.ACTION_DATA_AVAILABLE.equals(action)) {
                    CURRENT_STATE = MenuState.CONNECTED;
                    invalidateOptionsMenu();
                    int dest = intent.getIntExtra(BLUETOOTH_SERVICE.DATA_CHARACTERISTIC_DESTINATION,0);
                    byte[] value = intent.getByteArrayExtra(BLUETOOTH_SERVICE.DATA_CHARACTERISTIC_VALUE);
                    float val = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat();

                    charX.append(String.valueOf(dest)+ String.valueOf(val));
                    charX.append("\n");

//                    ValX.add(val);
//                    if(dest == 1){
//                        ValX.add(val);
//                    }
//                    if(dest == 2){
//                        ValY.add(val);
//                    }
//                    if(dest == 3){
//                        ValZ.add(val);
//                    }
                }
                if(BLUETOOTH_SERVICE.ACTION_GATT_DISCONNECTED.equals(action)){
                    CURRENT_STATE = MenuState.DISCONNECTED;
                    invalidateOptionsMenu();

                }
            }
        };
        registerReceiver(GattUpdate,filter);

        BLUETOOTH_SERVICE = new BluetoothService(BLUETOOTH_DEVICE,theContext);
        BLUETOOTH_SERVICE.StartService();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void serviceListAdd(String newService) {
        if(newService!=null){
            servicesList.add(newService);
        }
    }

    private void SetActionBarTitles(){
        if(BLUETOOTH_NAME != null){
            getSupportActionBar().setTitle(BLUETOOTH_NAME);
        }
        if(BLUETOOTH_NAME == null){
            getSupportActionBar().setTitle(BLUETOOTH_MAC);
        }
        getSupportActionBar().setSubtitle(CURRENT_STATE.toString());
    }

    @Override
    protected void onDestroy() {
        if (GattUpdate != null) {
            unregisterReceiver(GattUpdate);
            GattUpdate = null;
        }
        BLUETOOTH_SERVICE.EndService();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        theMenu = menu;
        UpdateMenuOptions(menu);
        return true;
    }
    private void UpdateMenuOptions(Menu menu){
        menu.findItem(R.id.bluetooth_search).setVisible(false);
        menu.findItem(R.id.bluetooth_status).setVisible(false);
        MenuItem stop = menu.findItem(R.id.aws_stop);
        MenuItem upload = menu.findItem(R.id.aws_upload);
        MenuItem read = menu.findItem(R.id.bluetooth_read);
        MenuItem write = menu.findItem(R.id.bluetooth_write);
        switch (CURRENT_STATE) {
            case CONNECTING:

                stop.setVisible(true);
                stop.setEnabled(false);

                upload.setVisible(true);
                upload.setEnabled(false);

                read.setVisible(true);
                read.setEnabled(false);

                write.setVisible(true);
                write.setEnabled(false);
                break;
            case CONNECTED:

                stop.setVisible(true);
                stop.setEnabled(false);

                upload.setVisible(true);
                upload.setEnabled(true);

                read.setVisible(false);
                read.setEnabled(false);

                write.setVisible(false);
                write.setEnabled(false);
                break;
            case DISCONNECTED:
                stop.setVisible(true);
                stop.setEnabled(false);

                upload.setVisible(true);
                upload.setEnabled(false);

                read.setVisible(true);
                read.setEnabled(true);

                write.setVisible(true);
                write.setEnabled(true);
                break;
            case UPLOADING:
                stop.setVisible(true);
                stop.setEnabled(true);

                upload.setVisible(true);
                upload.setEnabled(false);

                read.setVisible(false);
                read.setEnabled(false);

                write.setVisible(false);
                write.setEnabled(false);
                break;
            case STOPPED:
                stop.setVisible(true);
                stop.setEnabled(false);

                upload.setVisible(true);
                upload.setEnabled(true);

                read.setVisible(false);
                read.setEnabled(false);

                write.setVisible(false);
                write.setEnabled(false);

                break;
            case RESUMED:
                stop.setVisible(true);
                stop.setEnabled(true);

                upload.setVisible(false);
                upload.setEnabled(false);

                read.setVisible(false);
                read.setEnabled(false);

                write.setVisible(false);
                write.setEnabled(false);

                break;
            case PAUSED:
                stop.setVisible(true);
                stop.setEnabled(true);

                upload.setVisible(false);
                upload.setEnabled(false);

                read.setVisible(false);
                read.setEnabled(false);

                write.setVisible(false);
                write.setEnabled(false);

                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.DisplayX:
                charX.append("X:\n");
                for(Float f : ValX){
                    charX.append(String.valueOf(f));
                    charX.append("\n");
                }
                charX.append("Y:\n");
                for(Float f : ValY){
                    charX.append(String.valueOf(f));
                    charX.append("\n");
                }
                charX.append("Z:\n");
                for(Float f : ValZ){
                    charX.append(String.valueOf(f));
                    charX.append("\n");
                }
                break;
            case R.id.aws_stop:
                break;
            case R.id.aws_upload:

                break;
            case R.id.bluetooth_read:
                BLUETOOTH_SERVICE.StartService();
                break;
            case R.id.bluetooth_write:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        UpdateMenuOptions(menu);
        SetActionBarTitles();
        return true;
    }

    public boolean StartUploadAWS(MenuItem item){

        UpdateMenuOptions(theMenu);
        return true;
    }

    public boolean StopUploadAWS(MenuItem item){
        UpdateMenuOptions(theMenu);
        return true;
    }
    public boolean PauseUploadAWS(MenuItem item){
        UpdateMenuOptions(theMenu);
        return true;
    }
    public boolean ResumeUploadAWS(MenuItem item){
        UpdateMenuOptions(theMenu);
        return true;
    }



}
