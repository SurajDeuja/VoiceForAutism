package com.bsu.android.acd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bsu.android.acd.Device;
import com.bsu.android.acd.R;

import java.util.ArrayList;

/**
 * Created by surajdeuja on 3/7/16.
 */
public class DevicesAdapter extends ArrayAdapter<Device> {
    public DevicesAdapter(Context context, Device[] objects) {
        super(context, 0, objects);
    }

    public DevicesAdapter(Context context, ArrayList<Device> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the device to add to listview
        Device device = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }

        TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
        TextView deviceIp = (TextView) convertView.findViewById(R.id.deviceIp);

        deviceIp.setText(device.getDeviceIp());
        deviceName.setText(device.getDeviceName());


        return convertView;
    }

    @Override
    public void add(Device object) {
        if(getPosition(object) < 0) {
            super.add(object);
        }
    }
}
