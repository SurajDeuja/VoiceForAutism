package com.bsu.android.acd;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NetworkService extends IntentService {
    private String TAG = "NetworkService/";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NetworkService() {
        super("Network Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Started Service");
        NetworkDiscovery acdDeviceDiscoveryTask = new NetworkDiscovery(this.getBaseContext());
        try {
            acdDeviceDiscoveryTask.discoverDevice();
        } catch (Exception e) {
            Log.d(TAG,"Could not discover device on the network");
        }
        Log.d(TAG, "Stopped Service");
    }
}
