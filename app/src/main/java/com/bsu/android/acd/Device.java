package com.bsu.android.acd;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by surajdeuja on 3/7/16.
 */
@Parcel
public class Device implements Serializable {
    private String deviceName;
    private String deviceIp;

    public Device() {
    }
    
    public Device(String name, String ip) {
        deviceName = name;
        deviceIp = ip;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public boolean equals(Object o) {
        Device d = (Device)o;
        return deviceName.equals(d.getDeviceName()) &&
                deviceIp.equals(d.deviceIp);
    }
}
