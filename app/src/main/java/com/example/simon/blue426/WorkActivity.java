package com.example.simon.blue426;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import static android.bluetooth.BluetoothDevice.BOND_NONE;

public class WorkActivity extends AppCompatActivity {

    private Context theContext;

    public static final String BLUETOOTH_MESSAGE = "NEW_DEVICE";
    public static BluetoothDevice BLUETOOTH_DEVICE;
    public static String BLUETOOTH_NAME;
    public static String BLUETOOTH_MAC;
    public static int BLUETOOTH_BONDSTATE;

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
        BLUETOOTH_BONDSTATE = BLUETOOTH_DEVICE.getBondState();
        CURRENT_STATE = (BLUETOOTH_BONDSTATE == BOND_NONE) ? MenuState.DISCONNECTED : MenuState.CONNECTED;

        SetActionBarTitles();



        BLUETOOTH_SERVICE = new BluetoothService(BLUETOOTH_DEVICE,theContext);
//        BLUETOOTH_SERVICE.StartService();


    }

    @Override
    protected void onResume(){
        super.onResume();

        GattUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(BLUETOOTH_SERVICE.ACTION_GATT_CONNECTED.equals(action)){
                    CURRENT_STATE = MenuState.CONNECTED;
                    invalidateOptionsMenu();
                }
                if(BLUETOOTH_SERVICE.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                    CURRENT_STATE = MenuState.CONNECTED;
                    invalidateOptionsMenu();


                }
                if(BLUETOOTH_SERVICE.ACTION_DATA_AVAILABLE.equals(action)) {
                    CURRENT_STATE = MenuState.CONNECTED;
                    invalidateOptionsMenu();


                }
                if(BLUETOOTH_SERVICE.ACTION_GATT_DISCONNECTED.equals(action)){
                    CURRENT_STATE = MenuState.DISCONNECTED;
                    invalidateOptionsMenu();


                }
            }
        };


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
        MenuItem pause = menu.findItem(R.id.aws_pause);
        MenuItem resume = menu.findItem(R.id.aws_resume);
        MenuItem stop = menu.findItem(R.id.aws_stop);
        MenuItem upload = menu.findItem(R.id.aws_upload);
        switch (CURRENT_STATE) {
            case CONNECTING:
                pause.setVisible(false);
                pause.setEnabled(false);
                resume.setVisible(false);
                resume.setEnabled(false);
                stop.setVisible(true);
                stop.setEnabled(false);
                upload.setVisible(true);
                upload.setEnabled(false);
                break;
            case CONNECTED:
                pause.setVisible(false);
                resume.setVisible(false);
                stop.setVisible(true);
                stop.setEnabled(false);
                upload.setVisible(true);
                upload.setEnabled(true);
                break;
            case DISCONNECTED:
                pause.setVisible(false);
                pause.setEnabled(false);
                resume.setVisible(false);
                resume.setEnabled(false);
                stop.setVisible(true);
                stop.setEnabled(false);
                upload.setVisible(true);
                upload.setEnabled(false);
                break;
            case UPLOADING:
                pause.setVisible(true);
                resume.setVisible(false);
                stop.setVisible(true);
                upload.setVisible(false);
                upload.setEnabled(false);
                break;
            case STOPPED:
                pause.setVisible(false);
                resume.setVisible(false);
                stop.setVisible(true);
                stop.setEnabled(false);
                upload.setVisible(true);
                break;
            case RESUMED:
                pause.setVisible(true);
                resume.setVisible(true);
                resume.setEnabled(false);
                stop.setVisible(true);
                upload.setVisible(false);
                break;
            case PAUSED:
                pause.setVisible(true);
                pause.setEnabled(false);
                resume.setVisible(true);
                stop.setVisible(true);
                upload.setVisible(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.aws_pause:
                break;
            case R.id.aws_resume:
                break;
            case R.id.aws_stop:
                break;
            case R.id.aws_upload:
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
