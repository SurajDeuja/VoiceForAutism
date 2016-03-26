package com.bsu.android.acd;

import com.bsu.android.acd.rpc.RpcClient;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by surajdeuja on 3/23/16.
 */
@Module
public class ApiModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    Gson provideGsonObject() {
        return new Gson();
    }

    @Provides
    @Singleton
    RpcClient provideRpcClient(OkHttpClient httpClient, Gson gson) {
        return new RpcClient(httpClient, gson);
    }
}
