package com.bsu.android.acd;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by surajdeuja on 3/7/16.
 */
public class JsonUtils {

    private static final String TAG = "JsonUtils";

    public static String createBroadcastPayload() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SERVICE", "ACD");
            jsonObject.put("TYPE", "BROADCAST");
            jsonObject.put("COMMAND","REQUEST");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static boolean isBroadcastAck(String msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            if (jsonObject.getString("SERVICE").equals("ACD") &&
                    jsonObject.getString("TYPE").equals("BROADCAST") &&
                    jsonObject.getString("COMMAND").equals("ACK")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getDeviceName(String msg) {
        String deviceName  = null;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            deviceName = jsonObject.getString("DEV_NAME");
        } catch (JSONException e) {
            Log.e(TAG, "No device field.");
        }
        return deviceName;
    }

    public static String getDeviceIp(String msg) {
        String deviceIp  = null;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            deviceIp = jsonObject.getString("DEV_IP");
        } catch (JSONException e) {
            Log.e(TAG, "No device field.");
        }
        return deviceIp;
    }


    public static Device getDevice(String msg) {
        String deviceName, deviceIp;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            deviceName = jsonObject.getString("DEV_NAME");
            deviceIp = jsonObject.getString("IP_ADDRESS");
        } catch (JSONException e) {
            Log.e(TAG, "Invalid device object");
            return null;
        }

        return new Device(deviceName, deviceIp);
    }


}
