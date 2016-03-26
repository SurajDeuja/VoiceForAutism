package com.bsu.android.acd.rpc;

import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by surajdeuja on 3/25/16.
 */
public class RpcResults {
    public static final String TAG = "RpcResults";

    public static List<Button> buttonListFromJson(String json, Gson gson) {
        Log.d(TAG, json);
        Button.ButtonArray btnArray = gson.fromJson(json, Button.ButtonArray.class);
        return btnArray.getButtons();
    }
}
