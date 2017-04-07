package com.example.simon.blue426;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by simon on 3/28/2017.
 */

public class AdapterLE extends BaseAdapter {
    private ArrayList<BluetoothDevice> leDevices;
    private LayoutInflater inflater;

    public AdapterLE(Context context) {
        super();
        leDevices = new ArrayList<BluetoothDevice>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addDevice(BluetoothDevice device) {
        if (!leDevices.contains(device)) {
            leDevices.add(device);
        }
    }

    @Override
    public int getCount() {
        return leDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return leDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public BluetoothDevice getDevice(int position) {
        return leDevices.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.le_device_item, null);
        TextView leName = (TextView) rowView.findViewById(R.id.device_name);
        TextView leAddr = (TextView) rowView.findViewById(R.id.device_address);
        BluetoothDevice device = leDevices.get(position);
        leAddr.setText(device.getAddress());
        leName.setText(device.getName());
        rowView.setTag(device);
        return rowView;
    }

    public  void clear(){ leDevices.clear();}

}