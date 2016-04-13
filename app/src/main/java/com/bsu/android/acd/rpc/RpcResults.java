package com.bsu.android.acd.rpc;

import android.util.Log;

import com.bsu.android.acd.pojo.DeviceButton;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by surajdeuja on 3/25/16.
 */
public class RpcResults {
    public static final String TAG = "RpcResults";

    public static List<DeviceButton> buttonListFromJson(String json, Gson gson) {
        Log.d(TAG, json);
        DeviceButton.ButtonArray btnArray = gson.fromJson(json, DeviceButton.ButtonArray.class);
        return btnArray.getDeviceButtons();
    }
}
