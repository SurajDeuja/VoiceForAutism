package com.bsu.android.acd;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

/**
 * Created by surajdeuja on 3/24/16.
 */
public class AcdApplication extends Application {
    private ApiComponent mApiComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiComponent = DaggerApiComponent.builder().build();
    }

    public ApiComponent getApiComponent() {
        return mApiComponent;
    }
}
