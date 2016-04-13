package com.bsu.android.acd;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by surajdeuja on 3/24/16.
 */
@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {
    void inject(DeviceViewActivity activity);
    void inject(EditButtonActivity activity);
}
