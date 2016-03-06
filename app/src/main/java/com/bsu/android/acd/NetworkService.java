package com.bsu.android.acd;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NetworkService extends IntentService {
    private String TAG = "NetworkService/";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NetworkService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Started Service");
        NetworkDiscovery acdDeviceDiscoveryTask = new NetworkDiscovery(this.getBaseContext());
        try {
            acdDeviceDiscoveryTask.sendBroadcast();
            acdDeviceDiscoveryTask.listenBroadcast();
        } catch (Exception e) {
            Log.d(TAG,"Could not disover device on the network");
        }
        Log.d(TAG, "Stopped Service");
    }
}
